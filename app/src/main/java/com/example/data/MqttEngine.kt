package com.example.data

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocketFactory

enum class MqttStatus {
    DISCONNECTED, CONNECTING, CONNECTED
}

data class MqttReceivedMessage(val topic: String, val payload: String)

class MqttEngine(private val repository: NexusRepository) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var engineJob: Job? = null
    private var keepAliveJob: Job? = null
    private var simulationJob: Job? = null

    private val _status = MutableStateFlow(MqttStatus.DISCONNECTED)
    val status: StateFlow<MqttStatus> = _status

    private val _messages = MutableSharedFlow<MqttReceivedMessage>(replay = 5)
    val messages: SharedFlow<MqttReceivedMessage> = _messages

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError

    private val _isOffline = MutableStateFlow(true)
    val isOffline: StateFlow<Boolean> = _isOffline

    init {
        startSimulationLoop()
    }

    fun connect() {
        if (_status.value == MqttStatus.CONNECTING) return
        if (_status.value == MqttStatus.CONNECTED) return
        _status.value = MqttStatus.CONNECTING
        _connectionError.value = null
        stopSimulationLoop()

        engineJob?.cancel()
        engineJob = scope.launch {
            var retryDelay = 1000L
            var retryCount = 0

            while (isActive && _status.value != MqttStatus.CONNECTED) {
                try {
                    val config = repository.getBrokerConfig()
                    Log.d("MqttEngine", "Connecting to ${config.host}:${config.port} (useTls=${config.useTls})")

                    val baseSocket = if (config.useTls) {
                        val sslFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                        sslFactory.createSocket()
                    } else {
                        Socket()
                    }

                    socket = baseSocket
                    baseSocket.connect(InetSocketAddress(config.host, config.port), 5000)
                    baseSocket.soTimeout = (config.keepAlive * 1000) + 5000

                    val outStream = baseSocket.getOutputStream()
                    val inStream = baseSocket.getInputStream()
                    outputStream = outStream
                    inputStream = inStream

                    sendConnectPacket(config, outStream)

                    val ackHeader = inStream.read()
                    if (ackHeader == -1) throw Exception("Broker closed connection immediately")
                    val ackLen = readRemainingLength(inStream)
                    if (ackLen < 0 || ackLen > 1024) throw Exception("Malformed or oversized CONNACK packet: $ackLen")
                    val ackBody = ByteArray(ackLen)
                    var bytesRead = 0
                    while (bytesRead < ackLen) {
                        val read = inStream.read(ackBody, bytesRead, ackLen - bytesRead)
                        if (read == -1) throw Exception("Stream ended during CONNACK")
                        bytesRead += read
                    }

                    if (ackBody[1].toInt() != 0) {
                        throw Exception("Broker refused connection with code: ${ackBody[1].toInt()}")
                    }

                    _status.value = MqttStatus.CONNECTED
                    _isOffline.value = false
                    retryCount = 0
                    Log.d("MqttEngine", "Successfully connected!")

                    val activeSubs = repository.getAllSubscriptions()
                    for (sub in activeSubs) {
                        sendSubscribePacket(sub.topic, outStream)
                    }

                    startKeepAliveLoop(config.keepAlive, outStream)

                    while (isActive) {
                        val packetHeader = inStream.read()
                        if (packetHeader == -1) break
                        val remLen = readRemainingLength(inStream)
                        if (remLen < 0 || remLen > 262144) throw Exception("Malformed or oversized packet frame: $remLen")
                        val body = ByteArray(remLen)
                        var bodyRead = 0
                        while (bodyRead < remLen) {
                            val chunk = inStream.read(body, bodyRead, remLen - bodyRead)
                            if (chunk == -1) break
                            bodyRead += chunk
                        }
                        handleInboundPacket(packetHeader, body)
                    }

                    throw Exception("Connection lost")

                } catch (e: Exception) {
                    val msg = "Connection error: ${e.message}"
                    Log.e("MqttEngine", "$msg")
                    _connectionError.value = msg
                    disconnectInternal()
                    _isOffline.value = true

                    if (isActive) {
                        retryCount++
                        val delay = retryDelay.coerceAtMost(60000L)
                        Log.d("MqttEngine", "Reconnecting in ${delay}ms (attempt $retryCount)")
                        delay(delay)
                        retryDelay = (retryDelay * 2).coerceAtMost(60000L)
                        _status.value = MqttStatus.CONNECTING
                    }
                }
            }
        }
    }

    fun disconnect() {
        engineJob?.cancel()
        scope.launch {
            disconnectInternal()
        }
    }

    fun close() {
        engineJob?.cancel()
        stopSimulationLoop()
        _status.value = MqttStatus.DISCONNECTED
        keepAliveJob?.cancel()
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e("MqttEngine", "Exception while closing streams: ${e.message}")
        }
        socket = null
        outputStream = null
        inputStream = null
        scope.cancel()
    }

    private fun disconnectInternal() {
        _status.value = MqttStatus.DISCONNECTED
        keepAliveJob?.cancel()
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e("MqttEngine", "Exception while closing streams: ${e.message}")
        }
        socket = null
        outputStream = null
        inputStream = null
        
        // Start simulation loop so offline users can see dynamic variables
        startSimulationLoop()
    }

    fun publish(topic: String, payload: String) {
        val outStream = outputStream
        if (_status.value == MqttStatus.CONNECTED && outStream != null) {
            scope.launch {
                try {
                sendPublishPacket(topic, payload, outStream)
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Publish failed: ${e.message}")
                }
            }
        } else {
            // Local echo for tactile offline feedback
            scope.launch {
                try {
                    _messages.emit(MqttReceivedMessage(topic, payload))
                    repository.insertMessageLog(topic, payload, isOffline = true)
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Local publish fallback database insert failure", e)
                }
            }
        }
    }

    fun subscribe(topic: String) {
        scope.launch {
            try {
                val outStream = outputStream
                if (_status.value == MqttStatus.CONNECTED && outStream != null) {
                    try {
                        sendSubscribePacket(topic, outStream)
                    } catch (e: Exception) {
                        Log.e("MqttEngine", "Subscription packet failed: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("MqttEngine", "Failed to subscribe: ${e.message}", e)
            }
        }
    }

    fun unsubscribe(topic: String) {
        // network unsubscription packet can be sent here if needed, otherwise no-op since VM removed it from DB
    }

    // --- Packet Struct Encoders ---

    private fun sendConnectPacket(config: MqttBrokerConfig, out: OutputStream) {
        val protoLevel = when (config.mqttVersion) {
            "MQTT v3.1" -> 3
            "MQTT v5.0" -> 5
            else -> 4 // default MQTT v3.1.1
        }
        val protoName = if (protoLevel == 3) "MQIsdp" else "MQTT"
        val protoNameBytes = protoName.toByteArray(Charsets.UTF_8)
        val clientIdBytes = config.clientId.toByteArray(Charsets.UTF_8)
        val usernameBytes = config.login.toByteArray(Charsets.UTF_8)
        val passwordBytes = config.senha.toByteArray(Charsets.UTF_8)

        var connectFlags = 0x02 // Clean Session
        val hasUser = config.login.isNotEmpty()
        val hasPass = config.senha.isNotEmpty()
        if (hasUser) connectFlags = connectFlags or 0x80
        if (hasPass) connectFlags = connectFlags or 0x40

        // MQTT v5.0 requires a Properties field in the variable header
        val isMqtt5 = protoLevel == 5
        val propsLen = if (isMqtt5) 1 else 0

        // Calculate lengths
        // Proto length (2 bytes) + "MQTT" (4 bytes) + protocol level (1 byte) + flags (1 byte) + keepAlive (2 bytes)
        var payloadLen = 2 + protoNameBytes.size + 1 + 1 + 2 + propsLen
        payloadLen += 2 + clientIdBytes.size
        if (hasUser) payloadLen += 2 + usernameBytes.size
        if (hasPass) payloadLen += 2 + passwordBytes.size

        out.write(0x10) // CONNECT frame type
        writeRemainingLength(out, payloadLen)

        // Protocol standard header
        out.write(0x00)
        out.write(protoNameBytes.size)
        out.write(protoNameBytes)
        out.write(protoLevel)
        out.write(connectFlags)
        out.write((config.keepAlive ushr 8) and 0xFF)
        out.write(config.keepAlive and 0xFF)

        // MQTT v5.0: empty Properties field (zero-length)
        if (isMqtt5) {
            out.write(0x00)
        }

        // Payload fields
        out.write((clientIdBytes.size ushr 8) and 0xFF)
        out.write(clientIdBytes.size and 0xFF)
        out.write(clientIdBytes)

        if (hasUser) {
            out.write((usernameBytes.size ushr 8) and 0xFF)
            out.write(usernameBytes.size and 0xFF)
            out.write(usernameBytes)
        }
        if (hasPass) {
            out.write((passwordBytes.size ushr 8) and 0xFF)
            out.write(passwordBytes.size and 0xFF)
            out.write(passwordBytes)
        }
        out.flush()
    }

    private fun sendSubscribePacket(topic: String, out: OutputStream) {
        val topicBytes = topic.toByteArray(Charsets.UTF_8)
        // Packet ID (2 bytes) + topic length (2 bytes) + topic bytes + QoS level index (1 byte)
        val payloadLen = 2 + 2 + topicBytes.size + 1

        out.write(0x82) // SUBSCRIBE packet (QoS 1 header index)
        writeRemainingLength(out, payloadLen)

        // Msg ID (using fixed 0x01)
        out.write(0x00)
        out.write(0x01)

        // Topic block
        out.write((topicBytes.size ushr 8) and 0xFF)
        out.write(topicBytes.size and 0xFF)
        out.write(topicBytes)
        out.write(0x00) // Requested QoS level 0

        out.flush()
    }

    private fun sendPublishPacket(topic: String, payload: String, out: OutputStream) {
        val topicBytes = topic.toByteArray(Charsets.UTF_8)
        val payloadBytes = payload.toByteArray(Charsets.UTF_8)
        val payloadLen = 2 + topicBytes.size + payloadBytes.size

        out.write(0x30) // PUBLISH QoS 0 type
        writeRemainingLength(out, payloadLen)

        // Topic field
        out.write((topicBytes.size ushr 8) and 0xFF)
        out.write(topicBytes.size and 0xFF)
        out.write(topicBytes)

        // Payload
        out.write(payloadBytes)
        out.flush()
    }

    private fun startKeepAliveLoop(sec: Int, out: OutputStream) {
        keepAliveJob?.cancel()
        keepAliveJob = scope.launch {
            val interval = (sec * 1000L).coerceAtLeast(10000L)
            while (isActive) {
                delay(interval)
                try {
                    out.write(0xC0) // PINGREQ packet
                    out.write(0x00)
                    out.flush()
                    Log.d("MqttEngine", "Sent PINGREQ to keep connection alive.")
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Ping failed: ${e.message}")
                    break
                }
            }
        }
    }

    private fun handleInboundPacket(header: Int, body: ByteArray) {
        val type = header and 0xF0
        if (type == 0x30) { // PUBLISH
            try {
                val topicLen = ((body[0].toInt() and 0xFF) shl 8) or (body[1].toInt() and 0xFF)
                val topic = String(body, 2, topicLen, Charsets.UTF_8)
                val payloadStart = 2 + topicLen
                val payloadLen = body.size - payloadStart
                val payload = String(body, payloadStart, payloadLen, Charsets.UTF_8)

                Log.d("MqttEngine", "Received PUBLISH: $topic -> $payload")
                scope.launch {
                    _messages.emit(MqttReceivedMessage(topic, payload))
                    repository.insertMessageLog(topic, payload, isOffline = false)
                }
            } catch (e: Exception) {
                Log.e("MqttEngine", "Failed to parse inbound PUBLISH: ${e.message}")
            }
        } else if (type == 0xD0) {
            Log.d("MqttEngine", "Received PINGRESP.")
        }
    }

    private fun readRemainingLength(ins: InputStream): Int {
        var multiplier = 1
        var value = 0
        do {
            val encodedByte = ins.read()
            if (encodedByte == -1) throw Exception("Stream Closed")
            value += (encodedByte and 127) * multiplier
            if (multiplier > 128 * 128 * 128) throw Exception("Malformed Remaining Length varint")
            multiplier *= 128
        } while ((encodedByte and 128) != 0)
        return value
    }

    private fun writeRemainingLength(out: OutputStream, length: Int) {
        var len = length
        do {
            var digit = len % 128
            len /= 128
            if (len > 0) {
                digit = digit or 128
            }
            out.write(digit)
        } while (len > 0)
    }

    // --- Dynamic Offline/Telemetry Simulation loop ---

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            var temp = 24.8f
            var humidity = 58
            var storage = 75
            var latency1 = 12
            var latency2 = 342

            while (isActive) {
                delay(3000)

                // 1. Simulate Temp fluctuation
                try {
                    temp += (Math.random().toFloat() - 0.5f) * 0.4f
                    temp = Math.round(temp * 10f) / 10f
                    if (temp < 15f) temp = 15f
                    if (temp > 40f) temp = 40f
                    _messages.emit(MqttReceivedMessage("sensor/temp_01", temp.toString()))
                    try {
                        repository.insertMessageLog("sensor/temp_01", temp.toString(), isOffline = true)
                    } catch (e: Exception) {
                        Log.e("MqttEngine", "Failed to insert simulated message log: Temp", e)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Error simulating temp: ${e.message}")
                }

                // 2. Simulate humidity fluctuation
                delay(1500)
                try {
                    if (Math.random() > 0.6) {
                        humidity += if (Math.random() > 0.5) 1 else -1
                        if (humidity < 20) humidity = 20
                        if (humidity > 95) humidity = 95
                        _messages.emit(MqttReceivedMessage("sensor/humidity_01", "$humidity"))
                        try {
                            repository.insertMessageLog("sensor/humidity_01", "$humidity", isOffline = true)
                        } catch (e: Exception) {
                            Log.e("MqttEngine", "Failed to insert simulated message log: Humidity", e)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Error simulating humidity: ${e.message}")
                }

                // 3. Simulate latency fluctuation
                delay(1500)
                try {
                    latency1 = (10 + (Math.random() * 5).toInt())
                    latency2 = (320 + (Math.random() * 50).toInt())
                    _messages.emit(MqttReceivedMessage("network/latency_01", "$latency1 ms / $latency2 ms"))
                    try {
                        repository.insertMessageLog("network/latency_01", "$latency1 ms / $latency2 ms", isOffline = true)
                    } catch (e: Exception) {
                        Log.e("MqttEngine", "Failed to insert simulated message log: Latency", e)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Error simulating latency: ${e.message}")
                }

                // 4. Simulate storage slowly
                try {
                    if (Math.random() > 0.95) {
                        storage += if (Math.random() > 0.5) 1 else -1
                        if (storage < 1) storage = 1
                        if (storage > 100) storage = 100
                        _messages.emit(MqttReceivedMessage("drive/capacity_01", "$storage"))
                        try {
                            repository.insertMessageLog("drive/capacity_01", "$storage", isOffline = true)
                        } catch (e: Exception) {
                            Log.e("MqttEngine", "Failed to insert simulated message log: Storage", e)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("MqttEngine", "Error simulating storage: ${e.message}")
                }
            }
        }
    }

    private fun stopSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = null
    }
}
