package com.example

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppScreen()
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2800)
        onTimeout()
    }

    val transition = rememberInfiniteTransition(label = "pulse_rings")
    
    val ring1Progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring1"
    )

    val ring2Progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1F)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(360.dp)) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f

            // Ring 1: Orange Wave (from main logo color)
            val r1 = maxRadius * ring1Progress
            val alpha1 = (1f - ring1Progress) * 0.7f
            drawCircle(
                color = Color(0xFFFF7A00).copy(alpha = alpha1),
                radius = r1,
                center = centerOffset,
                style = Stroke(width = 3.dp.toPx())
            )

            // Ring 2: Cyan Wave (from logo's secondary details)
            val r2 = maxRadius * ring2Progress
            val alpha2 = (1f - ring2Progress) * 0.5f
            drawCircle(
                color = Color(0xFF00DBE9).copy(alpha = alpha2),
                radius = r2,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Ring 3: Soft Lime Cyan Accent
            val r3 = maxRadius * ((ring1Progress + 0.5f) % 1f)
            val alpha3 = (1f - ((ring1Progress + 0.5f) % 1f)) * 0.3f
            drawCircle(
                color = Color(0xFF00FFCC).copy(alpha = alpha3),
                radius = r3,
                center = centerOffset,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Box(
            modifier = Modifier
                .size(190.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF7A00).copy(alpha = 0.25f),
                            Color(0xFF00DBE9).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFF00DBE9).copy(alpha = 0.6f), CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.img_iot_logo),
                contentDescription = "Nexus IoT Launcher Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            Text(
                text = "AUTOMAÇÃO IoT",
                color = Color(0xFF00DBE9),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "GERENCIADOR MULTI-TÓPICO MQTT",
                color = Color(0xFFABD600),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF151B2D))
            ) {
                val loadingBarProgress by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "loading_bar"
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(loadingBarProgress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF7A00), Color(0xFF00DBE9))
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AppScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: NexusViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(app))

    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val widgets by viewModel.widgets.collectAsState()
    val messageLogs by viewModel.messageLogs.collectAsState()
    val telemetrySources by viewModel.telemetrySources.collectAsState()
    val telemetryHistory by viewModel.telemetryHistory.collectAsState()
    val brokerConfig by viewModel.brokerConfigState.collectAsState()
    val connectionError by viewModel.connectionError.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    LaunchedEffect(connectionError) {
        connectionError?.let { error ->
            Toast.makeText(context, "Falha na conexão: $error", Toast.LENGTH_LONG).show()
        }
    }

    var currentTab by remember { mutableStateOf("dashboard") } // "dashboard", "logs", "metrics", "device", "speak"
    var showBrokerSettingsDialog by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }

    if (showSplashScreen) {
        SplashScreen(onTimeout = { showSplashScreen = false })
    } else {
        Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonTheme.Background),
        topBar = {
            TopHeader(
                status = connectionStatus,
                onSettingsClick = { showBrokerSettingsDialog = true },
                onToggleConnection = { viewModel.toggleConnection() }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                activeTab = currentTab,
                onTabSelect = { currentTab = it }
            )
        },
        containerColor = NeonTheme.Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                "dashboard" -> DashboardScreen(
                    widgets = widgets,
                    isOffline = isOffline,
                    onToggleWidget = { viewModel.toggleWidget(it) },
                    onDeleteWidget = { viewModel.deleteWidget(it) },
                    onToggleConnection = { viewModel.toggleConnection() }
                )
                "logs" -> LogScreen(
                    logs = messageLogs,
                    onClearLogs = { viewModel.clearLogs() }
                )
                "metrics" -> MetricsScreen(
                    widgets = widgets,
                    telemetrySources = telemetrySources,
                    telemetryHistory = telemetryHistory,
                    isOffline = isOffline,
                    onDeleteWidget = { viewModel.deleteWidget(it) },
                    onRenameGauge = { widget, newTitle ->
                        viewModel.editWidget(
                            widget, newTitle, widget.topic, widget.type,
                            widget.payloadOn, widget.payloadOff, widget.widgetSize,
                            widget.colorHex, widget.imageOnUri, widget.imageOffUri,
                            widget.subscribeTopic, widget.imageSize
                        )
                    },
                    onAddTelemetrySource = { topic, label, colorHex -> viewModel.addTelemetrySource(topic, label, colorHex) },
                    onRemoveTelemetrySource = { viewModel.removeTelemetrySource(it) },
                    onToggleSimulation = { viewModel.toggleConnection() }
                )
                "device" -> DeviceConfigScreen(
                    viewModel = viewModel
                )
                "speak" -> VoiceScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Settings / Broker parameters editing dialog
    if (showBrokerSettingsDialog) {
        BrokerSettingsDialog(
            config = brokerConfig,
            onDismiss = { showBrokerSettingsDialog = false },
            onSave = { updated ->
                viewModel.updateBrokerFields(
                    serverName = updated.serverName,
                    clientId = updated.clientId,
                    host = updated.host,
                    port = updated.port,
                    login = updated.login,
                    senha = updated.senha,
                    keepAlive = updated.keepAlive,
                    mqttVersion = updated.mqttVersion,
                    useTls = updated.useTls
                )
                Toast.makeText(context, "Configurações do Broker Gravadas!", Toast.LENGTH_SHORT).show()
                showBrokerSettingsDialog = false
            },
            onSaveAndConnect = { updated ->
                viewModel.saveAndConnect(updated)
                Toast.makeText(context, "Configurações Gravadas! Conectando...", Toast.LENGTH_SHORT).show()
                showBrokerSettingsDialog = false
            }
        )
    }

    }
}

// --- Header Component ---
@Composable
fun TopHeader(
    status: MqttStatus,
    onSettingsClick: () -> Unit,
    onToggleConnection: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF0C1324))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.img_iot_logo),
                contentDescription = "Automação IoT Logo",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AUTOMAÇÃO IoT",
                color = NeonTheme.OutlineCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Connection Orb Status Indicator
            val statusColor = when (status) {
                MqttStatus.CONNECTED -> NeonTheme.OutlineLime
                MqttStatus.CONNECTING -> NeonTheme.OutlineCyan
                MqttStatus.DISCONNECTED -> Color(0xFFFF5252)
            }
            val statusText = when (status) {
                MqttStatus.CONNECTED -> "LIVE"
                MqttStatus.CONNECTING -> "..."
                MqttStatus.DISCONNECTED -> "OFF"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = statusText,
                    color = NeonTheme.TextVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }

            val buttonText = when (status) {
                MqttStatus.CONNECTED -> "DESCONECTAR"
                MqttStatus.CONNECTING -> "CONECTANDO"
                MqttStatus.DISCONNECTED -> "CONECTAR"
            }
            val buttonColor = when (status) {
                MqttStatus.CONNECTED -> Color(0xFFFF5252)
                MqttStatus.CONNECTING -> Color.Gray
                MqttStatus.DISCONNECTED -> NeonTheme.OutlineLime
            }
            Button(
                onClick = onToggleConnection,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor.copy(alpha = 0.15f), contentColor = buttonColor),
                border = BorderStroke(1.dp, buttonColor.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Text(text = buttonText, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuração do Broker",
                    tint = NeonTheme.OutlineCyan
                )
            }
        }
    }
    }
}

// --- Bottom Navigation Component ---
@Composable
fun BottomNavigationBar(
    activeTab: String,
    onTabSelect: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF070D1F),
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        val navItems = listOf(
            Triple("dashboard", "Grid", Icons.Default.Home),
            Triple("logs", "Logs", Icons.AutoMirrored.Filled.List),
            Triple("metrics", "Métricas", Icons.Default.Info),
            Triple("device", "Aparelho", Icons.Default.Build),
            Triple("speak", "Comandos", Icons.Default.PlayArrow)
        )

        navItems.forEach { (tabId, label, icon) ->
            val isActive = activeTab == tabId
            NavigationBarItem(
                selected = isActive,
                onClick = { onTabSelect(tabId) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isActive) NeonTheme.OutlineLime else NeonTheme.TextVariant.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) NeonTheme.OutlineLime else NeonTheme.TextVariant.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NeonTheme.OutlineLime.copy(alpha = 0.15f)
                )
            )
        }
    }
}

// --- Helper: load bitmap from URI ---
private fun loadWidgetBitmap(context: android.content.Context, uriString: String): android.graphics.Bitmap? {
    if (uriString.isBlank()) return null
    return try {
        if (uriString.startsWith("/")) {
            BitmapFactory.decodeFile(uriString)
        } else if (uriString.startsWith("content://")) {
            context.contentResolver.openInputStream(Uri.parse(uriString))?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

// --- Dashboard ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    widgets: List<WidgetConfig>,
    isOffline: Boolean,
    onToggleWidget: (WidgetConfig) -> Unit,
    onDeleteWidget: (WidgetConfig) -> Unit = {},
    onToggleConnection: () -> Unit = {}
) {
    var isDeleteMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "PAINEL DE CONTROLE", fontSize = 11.sp, color = NeonTheme.OutlineLime, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "Dashboard Interativo", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (widgets.isNotEmpty()) {
                TextButton(onClick = { isDeleteMode = !isDeleteMode }) {
                    Text(
                        text = if (isDeleteMode) "Concluir" else "Excluir",
                        fontSize = 12.sp,
                        color = if (isDeleteMode) NeonTheme.OutlineLime else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onToggleConnection,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOffline) Color(0xFFFF5252) else NeonTheme.OutlineLime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = if (isOffline) Icons.Default.Close else Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isOffline) "CONECTAR AO BROKER" else "DESCONECTAR",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        if (widgets.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                Text("Nenhum botão configurado. Crie um na aba Aparelhos.", color = NeonTheme.TextVariant, fontSize = 14.sp)
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                widgets.forEach { widget ->
                    val context = LocalContext.current
                    var onBmp by remember(widget.id, widget.imageOnUri) { mutableStateOf<android.graphics.Bitmap?>(null) }
                    var offBmp by remember(widget.id, widget.imageOffUri) { mutableStateOf<android.graphics.Bitmap?>(null) }
                    LaunchedEffect(widget.id, widget.imageOnUri) {
                        onBmp = loadWidgetBitmap(context, widget.imageOnUri)
                    }
                    LaunchedEffect(widget.id, widget.imageOffUri) {
                        offBmp = loadWidgetBitmap(context, widget.imageOffUri)
                    }

                    val isOn = widget.lastKnownValue == "ON" || widget.lastKnownValue == widget.payloadOn
                    val displayBmp = if (isOn) (onBmp ?: offBmp) else (offBmp ?: onBmp)

                    Box {
                        Column(
                            modifier = Modifier.clickable { onToggleWidget(widget) },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (displayBmp != null) {
                                androidx.compose.foundation.Image(
                                    bitmap = displayBmp.asImageBitmap(),
                                    contentDescription = widget.title,
                                    modifier = Modifier
                                        .size(widget.imageSize.dp.coerceIn(32.dp, 256.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(NeonTheme.CardBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(widget.title.take(2).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonTheme.OutlineCyan)
                                }
                            }
                            Text(
                                text = widget.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (isDeleteMode) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Excluir",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .clickable { onDeleteWidget(widget) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Telemetry Logs Screen ---
@Composable
fun LogScreen(
    logs: List<MqttMessageLog>,
    onClearLogs: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "REGISTRO DE EVENTOS", fontSize = 11.sp, color = NeonTheme.OutlineLime, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Logs de Mensagens", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onClearLogs) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Limpar logs", tint = Color(0xFFFF5252))
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum log registrado.", color = NeonTheme.TextVariant, fontSize = 14.sp)
                }
            }
        } else {
            items(logs) { log ->
                GlassCard(borderColor = Color(0x33FFFFFF)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.topic, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Monospace)
                        Text(
                            text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp)),
                            fontSize = 9.sp,
                            color = NeonTheme.TextVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(log.payload, fontSize = 13.sp, color = NeonTheme.TextVariant, fontFamily = FontFamily.Monospace)
                    if (log.isOfflineTelemetry) {
                        Spacer(Modifier.height(4.dp))
                        Text("SIMULADO", fontSize = 8.sp, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 9.sp, color = NeonTheme.TextVariant, letterSpacing = 1.sp)
    }
}

// --- Metrics / Telemetry Screen ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetricsScreen(
    widgets: List<WidgetConfig>,
    telemetrySources: List<TelemetrySource>,
    telemetryHistory: Map<String, List<Float>>,
    isOffline: Boolean,
    onDeleteWidget: (WidgetConfig) -> Unit,
    onRenameGauge: (WidgetConfig, String) -> Unit,
    onAddTelemetrySource: (topic: String, label: String, colorHex: String) -> Unit,
    onRemoveTelemetrySource: (TelemetrySource) -> Unit,
    onToggleSimulation: () -> Unit = {}
) {
    var showAddTelemetryDialog by remember { mutableStateOf(false) }
    var editingGauge by remember { mutableStateOf<WidgetConfig?>(null) }
    val sensorWidgets = widgets.filter {
        it.type == "temperature" || it.type == "humidity" || it.type == "gauge" || it.type == "pressure"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header ──
        item {
            Text(
                text = "DISPOSITIVO E SENSORIAMENTO",
                fontSize = 11.sp,
                color = NeonTheme.OutlineLime,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                 text = "Métricas Customizadas",
                 fontSize = 22.sp,
                 color = Color.White,
                 fontWeight = FontWeight.Bold
            )
        }

        // ── Gauge Cards ──
        if (sensorWidgets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma métrica de sensor cadastrada.",
                        color = NeonTheme.TextVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            val chunks = sensorWidgets.chunked(2)
            chunks.forEach { pair ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        pair.forEach { widget ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { editingGauge = widget }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val rawVal = widget.lastKnownValue.ifEmpty { "0" }
                                    val value = rawVal.replace(",", ".").toFloatOrNull() ?: 0f
                                    val maxValue = when (widget.type) {
                                        "temperature" -> 100f
                                        "humidity" -> 100f
                                        "gauge" -> 100f
                                        "pressure" -> 1000f
                                        else -> 100f
                                    }
                                    val unit = when (widget.type) {
                                        "temperature" -> "°C"
                                        "humidity" -> "% RH"
                                        "gauge" -> "%"
                                        "pressure" -> "hPa"
                                        else -> ""
                                    }
                                    val color = try {
                                        Color(android.graphics.Color.parseColor(widget.colorHex))
                                    } catch (e: Exception) {
                                        NeonTheme.OutlineCyan
                                    }
                                    CircularGauge(
                                        value = value,
                                        maxValue = maxValue,
                                        title = widget.title,
                                        unit = unit,
                                        color = color
                                    )
                                    Text(
                                        text = "Toque e segure para editar",
                                        fontSize = 7.sp,
                                        color = NeonTheme.TextVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // ── Telemetry Source Charts ──
        if (telemetrySources.isEmpty()) {
            item {
                GlassCard(borderColor = NeonTheme.OutlineCyan.copy(alpha = 0.3f)) {
                    Column {
                        Text(
                            text = "TELEMETRIA EM TEMPO REAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhuma fonte de telemetria configurada.",
                            fontSize = 12.sp,
                            color = NeonTheme.TextVariant
                        )
                    }
                }
            }
        } else {
            telemetrySources.forEach { source ->
                item {
                    val history = telemetryHistory[source.topic] ?: emptyList()
                    val currentVal = history.lastOrNull()
                    val minVal = history.minOrNull()
                    val maxVal = history.maxOrNull()
                    val sourceColor = try {
                        Color(android.graphics.Color.parseColor(source.colorHex))
                    } catch (e: Exception) {
                        NeonTheme.OutlineCyan
                    }

                    GlassCard(borderColor = sourceColor.copy(alpha = 0.3f)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = source.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                                    Text(text = source.topic, fontSize = 9.sp, color = NeonTheme.TextVariant, fontFamily = FontFamily.Monospace)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(if (isOffline) Color(0xFFFFB74D) else NeonTheme.OutlineLime, RoundedCornerShape(3.dp))
                                    )
                                    Text(
                                        text = if (isOffline) "SIMULAÇÃO" else "LIVE",
                                        fontSize = 8.sp,
                                        color = if (isOffline) Color(0xFFFFB74D) else NeonTheme.OutlineLime,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                    IconButton(
                                        onClick = { onRemoveTelemetrySource(source) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remover fonte",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SimulatedTelemetryChart(
                                points = history,
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                color = sourceColor,
                                showPlaceholder = history.size < 2,
                                placeholderLabel = "Aguardando dados em ${source.topic}..."
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TelemetryStat(label = "Atual", value = currentVal?.let { String.format("%.1f", it) } ?: "---", color = sourceColor)
                                TelemetryStat(label = "Mín", value = minVal?.let { String.format("%.1f", it) } ?: "---", color = Color(0xFF4FC3F7))
                                TelemetryStat(label = "Máx", value = maxVal?.let { String.format("%.1f", it) } ?: "---", color = Color(0xFFFF5252))
                                TelemetryStat(label = "Amostras", value = "${history.size}", color = NeonTheme.TextVariant)
                            }
                        }
                    }
                }
            }
        }

        // ── Add Telemetry Source button ──
        item {
            Button(
                onClick = { showAddTelemetryDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonTheme.OutlineCyan.copy(alpha = 0.1f),
                    contentColor = NeonTheme.OutlineCyan
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, NeonTheme.OutlineCyan.copy(alpha = 0.2f))
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Adicionar Fonte de Telemetria", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

    }

    // ── Edit Gauge Dialog ──
    editingGauge?.let { widget ->
        EditGaugeDialog(
            widget = widget,
            onDismiss = { editingGauge = null },
            onSave = { title ->
                onRenameGauge(widget, title.uppercase())
                editingGauge = null
            },
            onDelete = {
                onDeleteWidget(widget)
                editingGauge = null
            }
        )
    }

    // ── Add Telemetry Source Dialog ──
    if (showAddTelemetryDialog) {
        AddTelemetrySourceDialog(
            onDismiss = { showAddTelemetryDialog = false },
            onAdd = { topic, label, colorHex ->
                onAddTelemetrySource(topic, label, colorHex)
                showAddTelemetryDialog = false
            }
        )
    }
}

@Composable
fun EditGaugeDialog(
    widget: WidgetConfig,
    onDismiss: () -> Unit,
    onSave: (title: String) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(widget.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151B2D),
        title = {
            Text("Editar Gauge", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome do gauge") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTheme.OutlineCyan,
                        unfocusedBorderColor = Color(0xFF2E3447),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonTheme.OutlineCyan,
                        unfocusedLabelColor = NeonTheme.TextVariant
                    )
                )

                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252).copy(alpha = 0.15f),
                        contentColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f))
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excluir Gauge", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonTheme.OutlineCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NeonTheme.TextVariant)
            }
        }
    )
}

@Composable
fun AddTelemetrySourceDialog(
    onDismiss: () -> Unit,
    onAdd: (topic: String, label: String, colorHex: String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#00DBE9") }

    val colorOptions = listOf(
        "#00DBE9" to "Ciano",
        "#ABD600" to "Verde",
        "#FFACE8" to "Rosa",
        "#FFB74D" to "Laranja",
        "#4FC3F7" to "Azul",
        "#FF5252" to "Vermelho"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151B2D),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text("Nova Fonte de Telemetria", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Nome do gráfico") },
                    placeholder = { Text("ex: TEMPERATURA") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTheme.OutlineCyan,
                        unfocusedBorderColor = Color(0xFF2E3447),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonTheme.OutlineCyan,
                        unfocusedLabelColor = NeonTheme.TextVariant
                    )
                )
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Tópico MQTT") },
                    placeholder = { Text("ex: sensor/temp_01") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonTheme.OutlineCyan,
                        unfocusedBorderColor = Color(0xFF2E3447),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = NeonTheme.OutlineCyan,
                        unfocusedLabelColor = NeonTheme.TextVariant
                    )
                )
                Text("Cor do gráfico:", fontSize = 11.sp, color = NeonTheme.TextVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { (hex, name) ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { NeonTheme.OutlineCyan })
                                .border(if (selectedColorHex == hex) BorderStroke(2.dp, Color.White) else BorderStroke(0.dp, Color.Transparent), RoundedCornerShape(6.dp))
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        val effectiveLabel = label.ifBlank { topic }
                        onAdd(topic, effectiveLabel.uppercase(), selectedColorHex)
                    }
                },
                enabled = topic.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonTheme.OutlineCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Adicionar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NeonTheme.TextVariant)
            }
        }
    )
}

// --- Device configuration and settings Screen ---
@Composable
fun DeviceConfigScreen(
    viewModel: NexusViewModel
) {
    var widgetTitle by remember { mutableStateOf("Meu Atalho") }
    var publishTopic by remember { mutableStateOf("ESP32/Interruptor2/Comando") }
    var subscribeTopic by remember { mutableStateOf("ESP32/Interruptor2/Estado") }
    var payloadOn by remember { mutableStateOf("1") }
    var payloadOff by remember { mutableStateOf("0") }
    var widgetType by remember { mutableStateOf("switch") }
    var widgetColor by remember { mutableStateOf("#00dbe9") }
    var widgetSize by remember { mutableStateOf(1) }
    var imageSize by remember { mutableStateOf(124f) }

    var imageOnUriStr by remember { mutableStateOf("") }
    var imageOffUriStr by remember { mutableStateOf("") }

    val context = LocalContext.current

    val imageOnPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            imageOnUriStr = it.toString()
        }
    }

    val imageOffPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            imageOffUriStr = it.toString()
        }
    }

    fun loadBitmapPreview(uriStr: String): android.graphics.Bitmap? {
        if (uriStr.isBlank()) return null
        return loadWidgetBitmap(context, uriStr)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "CONFIGURAÇÃO GERAL E ATALHOS",
                fontSize = 11.sp,
                color = NeonTheme.OutlineCyan,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                 text = "Configuração do Dispositivo",
                 fontSize = 22.sp,
                 color = Color.White,
                 fontWeight = FontWeight.Bold
            )
        }

        item {
            GlassCard(borderColor = NeonTheme.OutlineCyan.copy(alpha = 0.3f)) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(text = "Configurações do Widget", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    OutlinedTextField(
                        value = widgetTitle,
                        onValueChange = { widgetTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("NOME DO ATALHO", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTheme.OutlineCyan,
                            unfocusedBorderColor = Color(0xFF2E3447),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = publishTopic,
                        onValueChange = { publishTopic = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("TÓPICO DE COMANDO (PUBLISH)", fontSize = 11.sp) },
                        placeholder = { Text("ESP32/Interruptor2/Comando", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTheme.OutlineCyan,
                            unfocusedBorderColor = Color(0xFF2E3447),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = subscribeTopic,
                        onValueChange = { subscribeTopic = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("TÓPICO DE ESTADO (SUBSCRIBE)", fontSize = 11.sp) },
                        placeholder = { Text("ESP32/Interruptor2/Estado", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTheme.OutlineCyan,
                            unfocusedBorderColor = Color(0xFF2E3447),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = payloadOn,
                        onValueChange = { payloadOn = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PAYLOAD ON / LIGAR", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTheme.OutlineCyan,
                            unfocusedBorderColor = Color(0xFF2E3447),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = payloadOff,
                        onValueChange = { payloadOff = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PAYLOAD OFF / DESLIGAR", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonTheme.OutlineCyan,
                            unfocusedBorderColor = Color(0xFF2E3447),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Text(text = "Tipo do Widget:", fontSize = 11.sp, color = NeonTheme.TextVariant, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("switch" to "LIGA/DESLIGA", "command" to "COMANDO ÚNICO").forEach { (tpId, lbl) ->
                            val isSel = widgetType == tpId
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) NeonTheme.OutlineCyan.copy(alpha = 0.15f) else Color(0xFF151B2D))
                                    .border(1.dp, if (isSel) NeonTheme.OutlineCyan else Color(0xFF2E3447), RoundedCornerShape(4.dp))
                                    .clickable { widgetType = tpId }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = lbl, fontSize = 10.sp, color = Color.White, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }

                    Text(text = "Cor do Widget:", fontSize = 11.sp, color = NeonTheme.TextVariant, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("#00dbe9" to "Azul", "#abd600" to "Verde", "#fface8" to "Rosa", "#ffffff" to "Branco").forEach { (hex, nm) ->
                            val isSel = widgetColor == hex
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) Color(android.graphics.Color.parseColor(hex)).copy(alpha = 0.15f) else Color(0xFF151B2D))
                                    .border(1.dp, if (isSel) Color(android.graphics.Color.parseColor(hex)) else Color(0xFF2E3447), RoundedCornerShape(4.dp))
                                    .clickable { widgetColor = hex }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = nm, fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Largura no Grid:", fontSize = 11.sp, color = NeonTheme.TextVariant)
                        Text(
                            text = if (widgetSize == 2) "2x (Largo)" else "1x (Compacto)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (widgetSize == 2) NeonTheme.OutlineLime else NeonTheme.TextVariant
                        )
                    }
                }
            }
        }

        item {
            GlassCard(borderColor = Color(0x33FFFFFF)) {
                Text(text = "Customização de Imagens", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { imageOnPicker.launch(arrayOf("image/*")) },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTheme.OutlineCyan.copy(alpha = 0.15f), contentColor = NeonTheme.OutlineCyan),
                            border = BorderStroke(1.dp, NeonTheme.OutlineCyan.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("IMAGEM ON", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val bmpOn = remember(imageOnUriStr) { loadBitmapPreview(imageOnUriStr) }
                        if (bmpOn != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bmpOn.asImageBitmap(),
                                contentDescription = "Imagem ON",
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF151B2D)).border(1.dp, Color(0xFF2E3447), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ON", fontSize = 10.sp, color = NeonTheme.TextVariant)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { imageOffPicker.launch(arrayOf("image/*")) },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x11FFFFFF), contentColor = Color.LightGray),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("IMAGEM OFF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val bmpOff = remember(imageOffUriStr) { loadBitmapPreview(imageOffUriStr) }
                        if (bmpOff != null) {
                            androidx.compose.foundation.Image(
                                bitmap = bmpOff.asImageBitmap(),
                                contentDescription = "Imagem OFF",
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF151B2D)).border(1.dp, Color(0xFF2E3447), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("OFF", fontSize = 10.sp, color = NeonTheme.TextVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tamanho do Botão/Atalho", fontSize = 11.sp, color = NeonTheme.TextVariant)
                    Text(text = "${imageSize.toInt()}px", fontSize = 12.sp, color = NeonTheme.OutlineCyan, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = imageSize,
                    onValueChange = {
                        imageSize = it
                        widgetSize = if (it > 144) 2 else 1
                    },
                    valueRange = 32f..256f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonTheme.OutlineCyan,
                        activeTrackColor = NeonTheme.OutlineCyan
                    )
                )
            }
        }

        item {
            GlassCard(borderColor = NeonTheme.OutlineLime.copy(alpha = 0.3f)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "PREVIEW ADAPTÁVEL", fontSize = 9.sp, color = NeonTheme.TextVariant, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    val previewBmp = remember(imageOnUriStr) { loadBitmapPreview(imageOnUriStr) }
                    if (previewBmp != null) {
                        androidx.compose.foundation.Image(
                            bitmap = previewBmp.asImageBitmap(),
                            contentDescription = "Preview",
                            modifier = Modifier
                                .size((imageSize.dp / 2).coerceAtMost(120.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, NeonTheme.OutlineLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size((imageSize.dp / 2).coerceAtMost(120.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF070D1F))
                                .border(1.dp, NeonTheme.OutlineLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            VectorIconHub(color = NeonTheme.OutlineLime, modifier = Modifier.fillMaxSize().padding(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = if (imageOnUriStr.isNotBlank()) "COM IMAGEM" else "PRONTO", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonTheme.OutlineLime)
                }
            }
        }

        item {
            GlowButton(
                text = "Salvar Widget na Grid",
                onClick = {
                    if (widgetTitle.isBlank() || publishTopic.isBlank()) {
                        Toast.makeText(context, "Preencha o nome e o tópico!", Toast.LENGTH_SHORT).show()
                        return@GlowButton
                    }
                    viewModel.addCustomWidget(
                        title = widgetTitle,
                        topic = publishTopic,
                        type = widgetType,
                        payloadOn = payloadOn,
                        payloadOff = payloadOff,
                        iconName = if (widgetType == "switch") "bolt" else "settings_remote",
                        size = widgetSize,
                        colorHex = widgetColor,
                        imageOnUri = imageOnUriStr,
                        imageOffUri = imageOffUriStr,
                        subscribeTopic = subscribeTopic,
                        imageSize = imageSize
                    )
                    Toast.makeText(context, "Widget '${widgetTitle}' salvo na Grid!", Toast.LENGTH_LONG).show()
                },
                color = NeonTheme.OutlineCyan
            )
        }
    }
}

// --- Voice controls Screen ---
@Composable
fun VoiceScreen(
    viewModel: NexusViewModel
) {
    val voiceStatus by viewModel.voiceStatus.collectAsState()
    val actionFeedback by viewModel.voiceActionFeedback.collectAsState()
    val commandResult by viewModel.commandResult.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (spokenText != null) {
                viewModel.executeVoiceCommand(spokenText)
            }
        }
    }

    val statusColor = when (voiceStatus) {
        "PRONTO" -> NeonTheme.OutlineCyan
        "OUVINDO..." -> NeonTheme.OutlineLime
        "PROCESSANDO..." -> Color(0xFFFFB74D)
        "SUCESSO" -> NeonTheme.OutlineLime
        "ERRO" -> Color(0xFFFF5252)
        else -> NeonTheme.TextVariant
    }

    LaunchedEffect(commandResult) {
        if (commandResult != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetVoiceStatus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = "ASSISTENTE VIRTUAL INTEGRADO",
                    fontSize = 11.sp,
                    color = NeonTheme.OutlineCyan,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                     text = "Nexus Voz",
                     fontSize = 22.sp,
                     color = Color.White,
                     fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Mic Button
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.15f))
                .border(2.dp, statusColor.copy(alpha = 0.6f), CircleShape)
                .clickable {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale um comando...")
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Reconhecimento de voz não disponível", Toast.LENGTH_SHORT).show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Falar comando",
                tint = statusColor,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status
        Text(
            text = voiceStatus,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            letterSpacing = 2.sp
        )

        // Action feedback
        if (actionFeedback.isNotEmpty()) {
            Text(
                text = actionFeedback,
                fontSize = 13.sp,
                color = NeonTheme.TextVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (voiceStatus == "PRONTO") {
            Text(
                text = "Toque no microfone e fale um comando de voz",
                fontSize = 12.sp,
                color = NeonTheme.TextVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- Dynamic Widget type specific icon resolver ---
@Composable
fun DynamicWidgetIcon(name: String, color: Color, modifier: Modifier = Modifier) {
    when (name) {
        "thermostat" -> Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = color, modifier = modifier)
        "bolt" -> Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = color, modifier = modifier)
        "storage" -> Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = color, modifier = modifier)
        "speed" -> Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = color, modifier = modifier)
        "restart_alt" -> Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = color, modifier = modifier)
        else -> Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = color, modifier = modifier)
    }
}

// --- Vector Tech drawing components ---
@Composable
fun VectorIconHub(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = 4.dp.toPx()

        val c1 = Offset(w / 2, h / 4)
        val c2 = Offset(w / 4, h * 3/4)
        val c3 = Offset(w * 3/4, h * 3/4)

        drawLine(color, c1, c2, strokeWidth = 2.dp.toPx())
        drawLine(color, c1, c3, strokeWidth = 2.dp.toPx())

        drawCircle(color, r, c1)
        drawCircle(color, r, c2)
        drawCircle(color, r, c3)
    }
}

@Composable
fun VectorIconMic(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.2f),
            size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )

        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.45f)
            quadraticTo(w * 0.2f, h * 0.75f, w * 0.5f, h * 0.75f)
            quadraticTo(w * 0.8f, h * 0.75f, w * 0.8f, h * 0.45f)
            moveTo(w * 0.5f, h * 0.75f)
            lineTo(w * 0.5f, h * 0.9f)
            moveTo(w * 0.3f, h * 0.9f)
            lineTo(w * 0.7f, h * 0.9f)
        }

        drawPath(path = path, color = color, style = Stroke(width = 3.dp.toPx()))
    }
}

// --- Full Dialog overlays ---

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun BrokerSettingsDialog(
    config: MqttBrokerConfig,
    onDismiss: () -> Unit,
    onSave: (MqttBrokerConfig) -> Unit,
    onSaveAndConnect: (MqttBrokerConfig) -> Unit
) {
    var serverName by remember(config) { mutableStateOf(config.serverName) }
    var clientId by remember(config) { mutableStateOf(config.clientId) }
    var host by remember(config) { mutableStateOf(config.host) }
    var portString by remember(config) { mutableStateOf(config.port.toString()) }
    var login by remember(config) { mutableStateOf(config.login) }
    var senha by remember(config) { mutableStateOf(config.senha) }
    var keepAliveString by remember(config) { mutableStateOf(config.keepAlive.toString()) }
    var mqttVersion by remember(config) { mutableStateOf(config.mqttVersion) }
    var useTls by remember(config) { mutableStateOf(config.useTls) }

    var expandedVersion by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonTheme.OutlineCyan,
        unfocusedBorderColor = NeonTheme.TextVariant.copy(alpha = 0.3f),
        focusedLabelColor = NeonTheme.OutlineCyan,
        unfocusedLabelColor = NeonTheme.TextVariant,
        cursorColor = NeonTheme.OutlineCyan,
        focusedTextColor = Color.White,
        unfocusedTextColor = NeonTheme.TextPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "CONEXÃO COM BROKER", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonTheme.OutlineCyan)
        },
        containerColor = NeonTheme.CardBackground,
        tonalElevation = 8.dp,
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nome do Servidor", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                        singleLine = true,
                        colors = fieldColors
                    )
                }
                item {
                    OutlinedTextField(
                        value = clientId,
                        onValueChange = { clientId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ID do Cliente", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                        singleLine = true,
                        colors = fieldColors
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = host,
                            onValueChange = { host = it },
                            modifier = Modifier.weight(2f),
                            label = { Text("Servidor/Host", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                            singleLine = true,
                            colors = fieldColors
                        )
                        OutlinedTextField(
                            value = portString,
                            onValueChange = { portString = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Porta", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = fieldColors
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = login,
                            onValueChange = { login = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Usuário", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                            singleLine = true,
                            colors = fieldColors
                        )
                        OutlinedTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Senha", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = fieldColors
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = keepAliveString,
                            onValueChange = { keepAliveString = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Keep Alive (segundos)", fontSize = 12.sp, color = NeonTheme.TextVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = fieldColors
                        )
                        Box(modifier = Modifier.weight(1f).padding(top = 6.dp)) {
                            Button(
                                onClick = { expandedVersion = true },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTheme.SurfaceDark),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(text = mqttVersion, fontSize = 12.sp, color = NeonTheme.TextPrimary)
                            }
                            DropdownMenu(
                                expanded = expandedVersion,
                                onDismissRequest = { expandedVersion = false },
                                modifier = Modifier.background(NeonTheme.CardBackground)
                            ) {
                                listOf("MQTT v3.1", "MQTT v3.1.1", "MQTT v5.0").forEach { ver ->
                                    DropdownMenuItem(
                                        text = { Text(ver, color = NeonTheme.TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            mqttVersion = ver
                                            expandedVersion = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TLS/SSL", fontSize = 13.sp, color = NeonTheme.TextPrimary)
                        Switch(
                            checked = useTls,
                            onCheckedChange = { useTls = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonTheme.OutlineCyan,
                                checkedTrackColor = NeonTheme.OutlineCyan.copy(alpha = 0.3f),
                                uncheckedThumbColor = NeonTheme.TextVariant,
                                uncheckedTrackColor = NeonTheme.TextVariant.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCELAR", color = NeonTheme.TextVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val finalPort = portString.toIntOrNull() ?: 1883
                            val finalKeepAlive = keepAliveString.toIntOrNull() ?: 60
                            onSave(
                                config.copy(
                                    serverName = serverName,
                                    clientId = clientId,
                                    host = host,
                                    port = finalPort,
                                    login = login,
                                    senha = senha,
                                    keepAlive = finalKeepAlive,
                                    mqttVersion = mqttVersion,
                                    useTls = useTls
                                )
                            )
                        },
                        border = BorderStroke(1.dp, NeonTheme.OutlineCyan.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonTheme.OutlineCyan),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("SALVAR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = {
                        val finalPort = portString.toIntOrNull() ?: 1883
                        val finalKeepAlive = keepAliveString.toIntOrNull() ?: 60
                        onSaveAndConnect(
                            config.copy(
                                serverName = serverName,
                                clientId = clientId,
                                host = host,
                                port = finalPort,
                                login = login,
                                senha = senha,
                                keepAlive = finalKeepAlive,
                                mqttVersion = mqttVersion,
                                useTls = useTls
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonTheme.OutlineCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("SALVAR E CONECTAR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        dismissButton = null
    )
}
