# Graph Report - MQTT-Dashboard-GoogleStudio  (2026-05-22)

## Corpus Check
- 24 files · ~23,029 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 227 nodes · 316 edges · 26 communities (15 shown, 11 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 29 edges (avg confidence: 0.82)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]

## God Nodes (most connected - your core abstractions)
1. `MqttDao` - 25 edges
2. `NexusViewModel` - 22 edges
3. `MainActivity` - 21 edges
4. `MqttEngine` - 21 edges
5. `NexusRepository` - 18 edges
6. `NexusRepository` - 18 edges
7. `AppScreen()` - 15 edges
8. `Instruções para Build e Instalação do MQTT Dashboard` - 11 edges
9. `MetricsScreen()` - 9 edges
10. `Comandos essenciais` - 8 edges

## Surprising Connections (you probably didn't know these)
- `Greeting Screenshot` --calls--> `SplashScreen()`  [INFERRED]
  app/src/test/screenshots/greeting.png → app/src/main/java/com/example/MainActivity.kt
- `Greeting Screenshot` --calls--> `MyApplicationTheme()`  [INFERRED]
  app/src/test/screenshots/greeting.png → app/src/main/java/com/example/ui/theme/Theme.kt
- `DashboardScreen()` --calls--> `CustomizableToggle()`  [INFERRED]
  app/src/main/java/com/example/MainActivity.kt → app/src/main/java/com/example/ui/UiComponents.kt
- `MetricsScreen()` --calls--> `CircularGauge()`  [INFERRED]
  app/src/main/java/com/example/MainActivity.kt → app/src/main/java/com/example/ui/UiComponents.kt
- `MetricsScreen()` --calls--> `SimulatedTelemetryChart()`  [INFERRED]
  app/src/main/java/com/example/MainActivity.kt → app/src/main/java/com/example/ui/UiComponents.kt

## Communities (26 total, 11 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.18
Nodes (26): AddTelemetrySourceDialog(), AddWidgetDialog(), AppScreen(), BottomNavigationBar(), BrokerSettingsDialog(), DashboardScreen(), DeviceConfigScreen(), DynamicWidgetIcon() (+18 more)

### Community 1 - "Community 1"
Cohesion: 0.12
Nodes (11): AppDatabase, getDatabase(), migrate(), MqttBrokerConfig, MqttMessageLog, MqttSubscription, TelemetrySource, NexusRepository (+3 more)

### Community 3 - "Community 3"
Cohesion: 0.20
Nodes (3): MqttEngine, MqttReceivedMessage, MqttStatus

### Community 5 - "Community 5"
Cohesion: 0.40
Nodes (4): description, majorCapabilities, name, requestFramePermissions

### Community 6 - "Community 6"
Cohesion: 1.00
Nodes (3): ic_launcher, ic_launcher_round, ic_launcher (xhdpi)

### Community 20 - "Community 20"
Cohesion: 0.08
Nodes (25): "A operação solicitada não pôde ser concluída devido a uma limitação do sistema", APK não instala no celular, code:powershell (winget install EclipseAdoptium.Temurin.21.JDK), code:powershell (# Capture o log completo do MQTT), code:powershell (java -version), code:powershell (# Criar diretório do SDK), code:powershell (# Definir JAVA_HOME (ajuste o nome da pasta se necessário)), code:powershell (# Verificar se o celular foi detectado) (+17 more)

### Community 21 - "Community 21"
Cohesion: 0.20
Nodes (9): Decisões, Fase 1: Adicionar campos de imagem ao WidgetConfig, Fase 2: Implementar image picker real, Fase 3: Corrigir "Salvar Dados", Fase 4: Exibir imagens no Grid (Dashboard), Fase 5: Build e verificação, Fases, Problemas (+1 more)

### Community 22 - "Community 22"
Cohesion: 0.38
Nodes (3): GreetingScreenshotTest, Greeting Screenshot, MyApplicationTheme()

### Community 25 - "Community 25"
Cohesion: 0.11
Nodes (17): Atualizar o grafo depois de mexer no código, code:bash (graphify update "."), code:bash (graphify extract "." --backend gemini), code:bash (graphify query "como funciona o fluxo de telemetria" --graph), code:bash (graphify path "DashboardScreen" "WidgetConfig" --graph graph), code:bash (graphify explain "loadWidgetBitmap" --graph graphify-out/gra), code:bash (graphify extract "." --force --backend gemini), Comandos essenciais (+9 more)

## Knowledge Gaps
- **42 isolated node(s):** `name`, `description`, `requestFramePermissions`, `majorCapabilities`, `NeonTheme` (+37 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NexusViewModel` connect `Community 4` to `Community 1`, `Community 3`?**
  _High betweenness centrality (0.111) - this node is a cross-community bridge._
- **Why does `TelemetrySource` connect `Community 1` to `Community 4`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Why does `MqttDao` connect `Community 2` to `Community 1`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `MqttEngine` (e.g. with `AndroidManifest.xml` and `NexusViewModel`) actually correct?**
  _`MqttEngine` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `name`, `description`, `requestFramePermissions` to the rest of the system?**
  _42 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.125 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.08695652173913043 - nodes in this community are weakly interconnected._