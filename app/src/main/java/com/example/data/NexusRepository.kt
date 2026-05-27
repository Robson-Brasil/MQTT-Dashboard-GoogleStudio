package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class NexusRepository(private val dao: MqttDao) {

    // Broker Configuration
    val brokerConfigFlow: Flow<MqttBrokerConfig> = dao.getBrokerConfigFlow().map { 
        it ?: MqttBrokerConfig() // Return default if not populated yet
    }

    suspend fun getBrokerConfig(): MqttBrokerConfig {
        return dao.getBrokerConfig() ?: MqttBrokerConfig().also {
            dao.saveBrokerConfig(it)
        }
    }

    suspend fun saveBrokerConfig(config: MqttBrokerConfig) {
        dao.saveBrokerConfig(config)
    }

    // Subscriptions
    val subscriptionsFlow: Flow<List<MqttSubscription>> = dao.getAllSubscriptionsFlow()

    suspend fun getAllSubscriptions(): List<MqttSubscription> {
        return dao.getAllSubscriptions()
    }

    suspend fun addSubscription(topic: String) {
        dao.addSubscription(MqttSubscription(topic = topic))
    }

    suspend fun removeSubscription(topic: String) {
        dao.removeSubscription(MqttSubscription(topic = topic))
    }

    // Telemetry and offline logs
    val messageLogsFlow: Flow<List<MqttMessageLog>> = dao.getAllMessageLogsFlow()

    suspend fun insertMessageLog(topic: String, payload: String, isOffline: Boolean = false) {
        dao.insertMessageLog(
            MqttMessageLog(topic = topic, payload = payload, isOfflineTelemetry = isOffline)
        )
        // Also automatically update any widget checking this topic (by subscribeTopic, same as ViewModel)
        dao.updateWidgetValueBySubscribeTopic(topic, payload)
    }

    suspend fun clearMessageLogs() {
        dao.clearMessageLogs()
    }

    // Widgets Dashboard Configuration
    val widgetsFlow: Flow<List<WidgetConfig>> = dao.getAllWidgetsFlow()

    suspend fun insertWidget(widget: WidgetConfig) {
        dao.insertWidget(widget)
    }

    suspend fun updateWidget(widget: WidgetConfig) {
        dao.updateWidget(widget)
    }

    suspend fun deleteWidget(widget: WidgetConfig) {
        dao.deleteWidget(widget)
    }

    suspend fun updateWidgetValue(topic: String, value: String) {
        dao.updateWidgetValueByTopic(topic, value)
    }

    suspend fun updateWidgetValueBySubscribeTopic(subscribeTopic: String, value: String) {
        dao.updateWidgetValueBySubscribeTopic(subscribeTopic, value)
    }

    suspend fun getMaxPosition(): Int {
        return dao.getMaxPosition()
    }

    suspend fun updateWidgetPosition(id: Int, newPos: Int) {
        return dao.updateWidgetPosition(id, newPos)
    }

    // Telemetry Sources
    val telemetrySourcesFlow: Flow<List<TelemetrySource>> = dao.getAllTelemetrySourcesFlow()

    suspend fun addTelemetrySource(topic: String, label: String, colorHex: String = "#00DBE9") {
        dao.insertTelemetrySource(TelemetrySource(topic = topic, label = label, colorHex = colorHex))
    }

    suspend fun removeTelemetrySource(source: TelemetrySource) {
        dao.deleteTelemetrySource(source)
    }
}
