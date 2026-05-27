package com.example.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = NexusRepository(database.mqttDao)
    val mqttEngine = MqttEngine(repository)
    private val voiceCommandManager = VoiceCommandManager(mqttEngine)

    // State Flows
    val connectionStatus: StateFlow<MqttStatus> = mqttEngine.status
    val widgets: StateFlow<List<WidgetConfig>> = repository.widgetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dashboardWidgets: StateFlow<List<WidgetConfig>> = widgets.map { list ->
        list.filter { it.type == "switch" || it.type == "command" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val messageLogs: StateFlow<List<MqttMessageLog>> = repository.messageLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<MqttSubscription>> = repository.subscriptionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Telemetry Sources (one chart per source)
    val telemetrySources: StateFlow<List<TelemetrySource>> = repository.telemetrySourcesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _telemetryHistory = MutableStateFlow<Map<String, List<Float>>>(emptyMap())
    val telemetryHistory: StateFlow<Map<String, List<Float>>> = _telemetryHistory

    // Voice Command Panel States
    private val _voiceStatus = MutableStateFlow("PRONTO")
    val voiceStatus: StateFlow<String> = _voiceStatus

    private val _voiceActionFeedback = MutableStateFlow("")
    val voiceActionFeedback: StateFlow<String> = _voiceActionFeedback

    private val _commandResult = MutableStateFlow<VoiceCommandManager.CommandResult?>(null)
    val commandResult: StateFlow<VoiceCommandManager.CommandResult?> = _commandResult

    // Connection Error state
    val connectionError: StateFlow<String?> = mqttEngine.connectionError
    val isOffline: StateFlow<Boolean> = mqttEngine.isOffline

    // Broker Configuration fields
    private val _brokerConfigState = MutableStateFlow(MqttBrokerConfig())
    val brokerConfigState: StateFlow<MqttBrokerConfig> = _brokerConfigState

    init {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val config = repository.getBrokerConfig()
                    _brokerConfigState.value = config
                    // Seed default telemetry source if none exist
                    val existing = repository.telemetrySourcesFlow.firstOrNull()
                    if (existing.isNullOrEmpty()) {
                        repository.addTelemetrySource("sensor/temp_01", "TEMPERATURA", "#00DBE9")
                    }
                }
            } catch (e: Exception) {
                Log.e("NexusViewModel", "Failed to seed default widgets or load broker config", e)
            }

            try {
                mqttEngine.messages.collect { msg ->
                    Log.d("NexusViewModel", "Broadcast message: ${msg.topic} -> ${msg.payload}")
                    // Update telemetry history for any matching source
                    telemetrySources.value.forEach { source ->
                        if (msg.topic == source.topic) {
                            msg.payload.toFloatOrNull()?.let { value ->
                                val current = _telemetryHistory.value[source.topic]?.toMutableList() ?: mutableListOf()
                                current.add(value)
                                if (current.size > 30) current.removeAt(0)
                                _telemetryHistory.value = _telemetryHistory.value + (source.topic to current)
                            }
                        }
                    }
                    widgets.value.firstOrNull { it.subscribeTopic == msg.topic }?.let {
                        repository.updateWidgetValueBySubscribeTopic(msg.topic, msg.payload)
                    }
                }
            } catch (e: Exception) {
                Log.e("NexusViewModel", "MQTT messages flow collection exception: ${e.message}", e)
            }
        }
    }

    // Connect/Disconnect controls
    fun toggleConnection() {
        viewModelScope.launch {
            if (connectionStatus.value == MqttStatus.CONNECTED) {
                mqttEngine.disconnect()
            } else {
                repository.saveBrokerConfig(_brokerConfigState.value)
                mqttEngine.connect()
            }
        }
    }

    fun saveAndConnect(config: MqttBrokerConfig) {
        _brokerConfigState.value = config
        viewModelScope.launch {
            repository.saveBrokerConfig(config)
            mqttEngine.disconnect()
            mqttEngine.connect()
        }
    }

    fun updateBrokerFields(
        serverName: String,
        clientId: String,
        host: String,
        port: Int,
        login: String,
        senha: String,
        keepAlive: Int,
        mqttVersion: String,
        useTls: Boolean
    ) {
        val updated = _brokerConfigState.value.copy(
            serverName = serverName,
            clientId = clientId,
            host = host,
            port = port,
            login = login,
            senha = senha,
            keepAlive = keepAlive,
            mqttVersion = mqttVersion,
            useTls = useTls
        )
        _brokerConfigState.value = updated
        viewModelScope.launch {
            repository.saveBrokerConfig(updated)
        }
    }

    // Subscriptions
    fun addCustomSubscription(topic: String) {
        if (topic.isBlank()) return
        viewModelScope.launch {
            repository.addSubscription(topic)
            mqttEngine.subscribe(topic)
        }
    }

    fun removeSubscription(topic: String) {
        viewModelScope.launch {
            repository.removeSubscription(topic)
            mqttEngine.unsubscribe(topic)
        }
    }

    // Clear logs
    fun clearLogs() {
        viewModelScope.launch {
            repository.clearMessageLogs()
        }
    }

    // Dynamic voice commands processing trigger
    fun executeVoiceCommand(commandText: String) {
        viewModelScope.launch {
            _voiceStatus.value = "OUVINDO..."
            _voiceActionFeedback.value = "\"$commandText\""
            kotlinx.coroutines.delay(1200)

            _voiceStatus.value = "PROCESSANDO..."
            val currentWidgets = widgets.value
            val result = voiceCommandManager.processCommand(commandText, currentWidgets)
            _commandResult.value = result

            if (result.success && result.topicPublished != null) {
                val matchedWidget = currentWidgets.find { it.topic == result.topicPublished }
                if (matchedWidget != null) {
                    val nextValue = when {
                        result.payloadPublished == matchedWidget.payloadOn -> "ON"
                        result.payloadPublished == matchedWidget.payloadOff -> "OFF"
                        else -> result.payloadPublished ?: "ON"
                    }
                    repository.updateWidgetValue(matchedWidget.topic, nextValue)
                }
            }

            _voiceStatus.value = if (result.success) "SUCESSO" else "ERRO"
            _voiceActionFeedback.value = result.actionTaken
        }
    }

    fun resetVoiceStatus() {
        _voiceStatus.value = "PRONTO"
        _voiceActionFeedback.value = ""
        _commandResult.value = null
    }

    // Telemetry Sources
    private fun inferGaugeType(label: String, topic: String): String {
        val combined = "$label $topic".lowercase()
        return when {
            "temp" in combined -> "temperature"
            "umid" in combined || "humid" in combined || "umidade" in combined -> "humidity"
            "press" in combined || "pressão" in combined || "pressao" in combined -> "pressure"
            else -> "gauge"
        }
    }

    fun addTelemetrySource(topic: String, label: String, colorHex: String = "#00DBE9") {
        viewModelScope.launch {
            repository.addTelemetrySource(topic, label, colorHex)
            val nextPos = repository.getMaxPosition() + 1
            val gaugeType = inferGaugeType(label, topic)
            repository.insertWidget(
                WidgetConfig(
                    title = label,
                    topic = topic,
                    type = gaugeType,
                    colorHex = colorHex,
                    subscribeTopic = topic,
                    lastKnownValue = "0",
                    position = nextPos,
                    imageSize = 124f
                )
            )
            mqttEngine.subscribe(topic)
            _telemetryHistory.value = _telemetryHistory.value + (topic to emptyList<Float>())
        }
    }

    fun removeTelemetrySource(source: TelemetrySource) {
        viewModelScope.launch {
            repository.removeTelemetrySource(source)
            widgets.value.find { it.topic == source.topic && it.type != "switch" && it.type != "command" }?.let {
                repository.deleteWidget(it)
            }
            _telemetryHistory.value = _telemetryHistory.value - source.topic
        }
    }

    // Add fully customized grid widgets to Room from front-end Form
    private suspend fun copyContentUriToFile(uriStr: String, prefix: String): String {
        if (uriStr.isBlank()) return ""
        if (!uriStr.startsWith("content://")) return uriStr
        return try {
            withContext(Dispatchers.IO) {
                val uri = Uri.parse(uriStr)
                val app = getApplication<Application>()
                val input = app.contentResolver.openInputStream(uri) ?: return@withContext ""
                val bitmap = android.graphics.BitmapFactory.decodeStream(input)
                input.close()
                if (bitmap == null) return@withContext ""
                val fileName = "${prefix}_${System.currentTimeMillis()}.png"
                app.openFileOutput(fileName, android.content.Context.MODE_PRIVATE).use { output ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output)
                }
                bitmap.recycle()
                app.filesDir.absolutePath + "/" + fileName
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun addCustomWidget(
        title: String,
        topic: String,
        type: String,
        payloadOn: String,
        payloadOff: String,
        iconName: String,
        size: Int,
        colorHex: String,
        imageOnUri: String = "",
        imageOffUri: String = "",
        subscribeTopic: String = "",
        imageSize: Float = 124f
    ) {
        viewModelScope.launch {
            val fileOn = copyContentUriToFile(imageOnUri, "widget_on")
            val fileOff = copyContentUriToFile(imageOffUri, "widget_off")
            val effectiveSubscribe = subscribeTopic.ifBlank { topic }
            val nextPos = repository.getMaxPosition() + 1
            val newWidget = WidgetConfig(
                title = title.uppercase(),
                topic = topic,
                type = type,
                payloadOn = payloadOn,
                payloadOff = payloadOff,
                iconName = iconName,
                widgetSize = size,
                colorHex = colorHex,
                lastKnownValue = if (type == "switch") "OFF" else "0",
                imageOnUri = fileOn,
                imageOffUri = fileOff,
                subscribeTopic = effectiveSubscribe,
                position = nextPos,
                imageSize = imageSize
            )
            repository.insertWidget(newWidget)
            mqttEngine.subscribe(effectiveSubscribe)
        }
    }

    fun editWidget(
        widget: WidgetConfig,
        title: String,
        topic: String,
        type: String,
        payloadOn: String,
        payloadOff: String,
        size: Int,
        colorHex: String,
        imageOnUri: String,
        imageOffUri: String,
        subscribeTopic: String,
        imageSize: Float = 124f
    ) {
        viewModelScope.launch {
            val fileOn = if (imageOnUri.startsWith("content://")) copyContentUriToFile(imageOnUri, "widget_on") else imageOnUri
            val fileOff = if (imageOffUri.startsWith("content://")) copyContentUriToFile(imageOffUri, "widget_off") else imageOffUri
            val updated = widget.copy(
                title = title.uppercase(),
                topic = topic,
                type = type,
                payloadOn = payloadOn,
                payloadOff = payloadOff,
                widgetSize = size,
                colorHex = colorHex,
                imageOnUri = fileOn,
                imageOffUri = fileOff,
                subscribeTopic = subscribeTopic.ifBlank { topic },
                imageSize = imageSize
            )
            repository.updateWidget(updated)
        }
    }

    fun deleteWidget(widget: WidgetConfig) {
        viewModelScope.launch {
            repository.deleteWidget(widget)
        }
    }

    fun moveWidgetUp(widget: WidgetConfig) {
        viewModelScope.launch {
            val widgets = repository.widgetsFlow.firstOrNull() ?: return@launch
            val idx = widgets.indexOfFirst { it.id == widget.id }
            if (idx <= 0) return@launch
            val above = widgets[idx - 1]
            repository.updateWidgetPosition(widget.id, above.position)
            repository.updateWidgetPosition(above.id, widget.position)
        }
    }

    fun moveWidgetDown(widget: WidgetConfig) {
        viewModelScope.launch {
            val widgets = repository.widgetsFlow.firstOrNull() ?: return@launch
            val idx = widgets.indexOfFirst { it.id == widget.id }
            if (idx < 0 || idx >= widgets.size - 1) return@launch
            val below = widgets[idx + 1]
            repository.updateWidgetPosition(widget.id, below.position)
            repository.updateWidgetPosition(below.id, widget.position)
        }
    }

    fun swapWidgets(indexA: Int, indexB: Int) {
        viewModelScope.launch {
            val widgets = repository.widgetsFlow.firstOrNull() ?: return@launch
            if (indexA < 0 || indexA >= widgets.size || indexB < 0 || indexB >= widgets.size) return@launch
            val a = widgets[indexA]
            val b = widgets[indexB]
            repository.updateWidgetPosition(a.id, b.position)
            repository.updateWidgetPosition(b.id, a.position)
        }
    }

    fun toggleWidget(widget: WidgetConfig) {
        val isCurrentlyOn = widget.lastKnownValue == "ON" || widget.lastKnownValue == widget.payloadOn
        val nextState = if (isCurrentlyOn) "OFF" else "ON"
        val payload = if (nextState == "ON") widget.payloadOn else widget.payloadOff
        mqttEngine.publish(widget.topic, payload)
        viewModelScope.launch {
            repository.updateWidgetValue(widget.topic, nextState)
        }
    }

    fun triggerWidgetCommand(widget: WidgetConfig) {
        if (widget.type == "switch") {
            val isCurrentlyOn = widget.lastKnownValue == "ON" || widget.lastKnownValue == widget.payloadOn
            val nextState = if (isCurrentlyOn) "OFF" else "ON"
            val payload = if (nextState == "ON") widget.payloadOn else widget.payloadOff
            mqttEngine.publish(widget.topic, payload)
            viewModelScope.launch {
                repository.updateWidgetValue(widget.topic, nextState)
            }
        } else if (widget.type == "command") {
            mqttEngine.publish(widget.topic, widget.payloadOn)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttEngine.close()
    }
}
