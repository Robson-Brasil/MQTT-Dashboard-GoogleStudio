# Graph Report - MQTT-Dashboard-GoogleStudio  (2026-05-27)

## Corpus Check
- 24 files · ~22,571 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 333 nodes · 456 edges · 40 communities (25 shown, 15 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 42 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `54a77dcc`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]

## God Nodes (most connected - your core abstractions)
1. `MqttDao` - 26 edges
2. `NexusViewModel` - 25 edges
3. `MqttEngine` - 22 edges
4. `MainActivity` - 21 edges
5. `NexusRepository` - 19 edges
6. `NexusRepository` - 18 edges
7. `NeonTheme` - 16 edges
8. `AppScreen()` - 15 edges
9. `AppScreen` - 15 edges
10. `Instruções para Build e Instalação do MQTT Dashboard` - 12 edges

## Surprising Connections (you probably didn't know these)
- `MyApplicationTheme()` --calls--> `Greeting Screenshot`  [INFERRED]
  C:/Users/RobsonBrasil/Desktop/MQTT-Dashboard-GoogleStudio/app/src/main/java/com/example/ui/theme/Theme.kt → app/src/test/screenshots/greeting.png
- `AutomaÃ§Ã£o IoT` --references--> `Graphify Project Config`  [INFERRED]
  metadata.json → AGENTS.md
- `SplashScreen()` --calls--> `Greeting Screenshot`  [INFERRED]
  app/src/main/java/com/example/MainActivity.kt → app/src/test/screenshots/greeting.png
- `MqttEngine` --calls--> `NexusViewModel`  [INFERRED]
  C:/Users/RobsonBrasil/Desktop/MQTT-Dashboard-GoogleStudio/app/src/main/java/com/example/data/MqttEngine.kt → app/src/main/java/com/example/ui/NexusViewModel.kt
- `MetricsScreen()` --calls--> `CircularGauge()`  [INFERRED]
  app/src/main/java/com/example/MainActivity.kt → C:/Users/RobsonBrasil/Desktop/MQTT-Dashboard-GoogleStudio/app/src/main/java/com/example/ui/UiComponents.kt

## Communities (40 total, 15 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.17
Nodes (27): AddTelemetrySourceDialog(), AddWidgetDialog(), AppScreen(), BottomNavigationBar(), BrokerSettingsDialog(), DashboardScreen(), DeviceConfigScreen(), DynamicWidgetIcon() (+19 more)

### Community 1 - "Community 1"
Cohesion: 0.13
Nodes (12): AppDatabase, getDatabase(), migrate(), MqttBrokerConfig, MqttMessageLog, MqttSubscription, TelemetrySource, WidgetConfig (+4 more)

### Community 3 - "Community 3"
Cohesion: 0.19
Nodes (3): MqttEngine, MqttReceivedMessage, MqttStatus

### Community 5 - "Community 5"
Cohesion: 0.53
Nodes (4): description, majorCapabilities, name, requestFramePermissions

### Community 6 - "Community 6"
Cohesion: 0.24
Nodes (10): ic_launcher, ic_launcher (mdpi), ic_launcher_round, ic_launcher_round (mdpi), ic_launcher_round (xhdpi), ic_launcher_round (xxhdpi), ic_launcher_round (xxxhdpi), ic_launcher (xhdpi) (+2 more)

### Community 20 - "Community 20"
Cohesion: 0.07
Nodes (25): "A operação solicitada não pôde ser concluída devido a uma limitação do sistema", APK não instala no celular, code:powershell (winget install EclipseAdoptium.Temurin.21.JDK), code:powershell (# Capture o log completo do MQTT), code:powershell (java -version), code:powershell (# Criar diretório do SDK), code:powershell (# Definir JAVA_HOME (ajuste o nome da pasta se necessário)), code:powershell (# Verificar se o celular foi detectado) (+17 more)

### Community 21 - "Community 21"
Cohesion: 0.18
Nodes (9): Decisões, Fase 1: Adicionar campos de imagem ao WidgetConfig, Fase 2: Implementar image picker real, Fase 3: Corrigir "Salvar Dados", Fase 4: Exibir imagens no Grid (Dashboard), Fase 5: Build e verificação, Fases, Problemas (+1 more)

### Community 22 - "Community 22"
Cohesion: 0.28
Nodes (3): GreetingScreenshotTest, Greeting Screenshot, MyApplicationTheme()

### Community 23 - "Community 23"
Cohesion: 0.05
Nodes (40): Android 14+ (API 34), Arquitetura, Banco de Dados (Room), Build, Build lento, code:block1 (com.example/), code:block2 (┌─────────────┐     ┌──────────────┐     ┌───────────────┐), code:bash (# Clonar) (+32 more)

### Community 25 - "Community 25"
Cohesion: 0.11
Nodes (17): Atualizar o grafo depois de mexer no código, code:bash (graphify update "."), code:bash (graphify extract "." --backend gemini), code:bash (graphify query "como funciona o fluxo de telemetria" --graph), code:bash (graphify path "DashboardScreen" "WidgetConfig" --graph graph), code:bash (graphify explain "loadWidgetBitmap" --graph graphify-out/gra), code:bash (graphify extract "." --force --backend gemini), Comandos essenciais (+9 more)

### Community 26 - "Community 26"
Cohesion: 0.14
Nodes (27): AddWidgetDialog, AppScreen, BottomNavigationBar, BrokerSettingsDialog, DashboardScreen, DeviceConfigScreen, DynamicWidgetIcon, EditWidgetDialog (+19 more)

### Community 27 - "Community 27"
Cohesion: 0.67
Nodes (3): Graphify Project Config, Graphify Guide, AutomaÃ§Ã£o IoT

## Knowledge Gaps
- **79 isolated node(s):** `code:bash (graphify update ".")`, `code:bash (graphify extract "." --backend gemini)`, `code:bash (graphify query "como funciona o fluxo de telemetria" --graph)`, `code:bash (graphify path "DashboardScreen" "WidgetConfig" --graph graph)`, `code:bash (graphify explain "loadWidgetBitmap" --graph graphify-out/gra)` (+74 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NexusViewModel` connect `Community 4` to `Community 1`, `Community 3`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Why does `TelemetrySource` connect `Community 1` to `Community 4`?**
  _High betweenness centrality (0.045) - this node is a cross-community bridge._
- **Why does `MqttDao` connect `Community 2` to `Community 1`?**
  _High betweenness centrality (0.037) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `MqttEngine` (e.g. with `AndroidManifest.xml` and `NexusViewModel`) actually correct?**
  _`MqttEngine` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `code:bash (graphify update ".")`, `code:bash (graphify extract "." --backend gemini)`, `code:bash (graphify query "como funciona o fluxo de telemetria" --graph)` to the rest of the system?**
  _79 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.12605042016806722 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.08695652173913043 - nodes in this community are weakly interconnected._