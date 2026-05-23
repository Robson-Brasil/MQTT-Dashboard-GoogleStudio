package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoiceCommandManager(private val mqttEngine: MqttEngine) {

    data class CommandResult(
        val success: Boolean,
        val commandProvided: String,
        val actionTaken: String,
        val topicPublished: String?,
        val payloadPublished: String?
    )

    suspend fun processCommand(input: String, widgets: List<WidgetConfig> = emptyList()): CommandResult = withContext(Dispatchers.IO) {
        val normalized = input.trim().lowercase()
        Log.d("VoiceCommand", "Processing text command: '$normalized'")

        // 0. Voice control by widget title (ligar/desligar + any widget title)
        val desligar = normalized.contains("desligar") || normalized.contains("desliga") || normalized.contains("desativar")
        val ligar = !desligar && (normalized.contains("ligar") || normalized.contains("liga") || normalized.contains("ativar") || normalized.contains("acionar"))
        if ((ligar || desligar) && widgets.isNotEmpty()) {
            val stopWords = setOf("ligar", "liga", "ativar", "acionar", "desligar", "desliga", "desativar", "o", "a", "os", "as", "de", "da", "do", "em", "para", "com", "um", "uma")
            fun matchWidget(nome: String): Boolean {
                if (normalized.contains(nome)) return true
                val nomeWords = nome.split(" ").filter { it.length > 2 && it !in stopWords }
                val inputWords = normalized.split(" ").filter { it.length > 2 && it !in stopWords }
                return nomeWords.any { nw -> inputWords.any { iw -> iw == nw || iw.contains(nw) || nw.contains(iw) } }
            }
            for (widget in widgets) {
                val nomeWidget = widget.title.trim().lowercase()
                if (matchWidget(nomeWidget)) {
                    val payload = if (ligar) widget.payloadOn else widget.payloadOff
                    val acao = if (ligar) "Ligado" else "Desligado"
                    mqttEngine.publish(widget.topic, payload)
                    return@withContext CommandResult(
                        success = true,
                        commandProvided = input,
                        actionTaken = "$acao: ${widget.title} via MQTT!",
                        topicPublished = widget.topic,
                        payloadPublished = payload
                    )
                }
            }
            // "ligar tudo" / "desligar tudo"
            if (normalized.contains("tudo") || normalized.contains("todos")) {
                val allOnPayload = widgets.firstOrNull()?.payloadOn ?: "1"
                val allOffPayload = widgets.firstOrNull()?.payloadOff ?: "0"
                val payload = if (ligar) allOnPayload else allOffPayload
                val acao = if (ligar) "Ligados" else "Desligados"
                for (widget in widgets) {
                    mqttEngine.publish(widget.topic, payload)
                }
                return@withContext CommandResult(
                    success = true,
                    commandProvided = input,
                    actionTaken = "Todos os ${widgets.size} widgets $acao!",
                    topicPublished = "${widgets.size} tópicos",
                    payloadPublished = payload
                )
            }
        }

        // 1. Efficient, instant, local rule-based matching
        when {
            normalized.contains("protocolo alfa") || normalized.contains("ativar protocolo") -> {
                mqttEngine.publish("system/protocol", "ALFA_ON")
                return@withContext CommandResult(
                    success = true,
                    commandProvided = input,
                    actionTaken = "Protocolo Alfa Ativado com Sucesso!",
                    topicPublished = "system/protocol",
                    payloadPublished = "ALFA_ON"
                )
            }
            normalized.contains("bateria") || normalized.contains("status de energia") -> {
                mqttEngine.publish("system/battery", "REQUEST")
                return@withContext CommandResult(
                    success = true,
                    commandProvided = input,
                    actionTaken = "Status de Bateria requisitado (Nível atual: 92%)",
                    topicPublished = "system/battery",
                    payloadPublished = "REQUEST"
                )
            }
            normalized.contains("sincronizar") || normalized.contains("sensores") -> {
                mqttEngine.publish("sensor/sync", "SYNC")
                return@withContext CommandResult(
                    success = true,
                    commandProvided = input,
                    actionTaken = "Sincronização de todos os sensores iniciada!",
                    topicPublished = "sensor/sync",
                    payloadPublished = "SYNC"
                )
            }
            normalized.contains("iluminação") || normalized.contains("luz") || normalized.contains("power") -> {
                mqttEngine.publish("switch/power_01", "TOGGLE")
                return@withContext CommandResult(
                    success = true,
                    commandProvided = input,
                    actionTaken = "Comando de chaveamento de iluminação enviado!",
                    topicPublished = "switch/power_01",
                    payloadPublished = "TOGGLE"
                )
            }
        }

        // 2. Fallback default
        CommandResult(
            success = false,
            commandProvided = input,
            actionTaken = "Comando não reconhecido. Tente 'Ativar Protocolo Alfa', 'Sincronizar Sensores', 'Mudança na iluminação' ou 'Status da Bateria'.",
            topicPublished = null,
            payloadPublished = null
        )
    }
}
