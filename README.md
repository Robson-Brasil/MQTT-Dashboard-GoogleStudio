<div align="center">
  <img width="120" height="120" alt="Nexus IoT Logo" src="app/src/main/res/drawable/img_iot_logo.png" style="border-radius: 60px;" />
  <h1 align="center">NexusCommand — MQTT IoT Dashboard</h1>
  <p align="center">
    <strong>Automação IoT com painel cyberpunk, telemetria em tempo real e comandos de voz</strong>
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/Kotlin-2.0-blueviolet?logo=kotlin" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Jetpack%20Compose-1.7-4285F4?logo=jetpackcompose" alt="Compose" />
    <img src="https://img.shields.io/badge/API-24%2B-brightgreen" alt="minSdk" />
    <img src="https://img.shields.io/badge/MQTT-v3.1%20%7C%20v3.1.1%20%7C%20v5.0-6600cc" alt="MQTT" />
    <img src="https://img.shields.io/badge/TLS-SSL-blue" alt="TLS" />
    <img src="https://img.shields.io/badge/license-MIT-green" alt="License" />
  </p>
</div>

---

## Visão Geral

**NexusCommand** é um dashboard Android nativo para automação residencial e industrial via protocolo **MQTT**. Construído com Jetpack Compose, oferece uma interface cyberpunk com resposta tátil, gráficos de telemetria animados em tempo real, suporte a múltiplos brokers e comandos de voz em português.

| | |
|---|---|
| **📱 Plataforma** | Android 7.0+ (API 24) |
| **🧩 UI** | Jetpack Compose + Material 3 |
| **🔌 Protocolo** | MQTT v3.1, v3.1.1, v5.0 (TCP raw socket) |
| **🔒 Segurança** | TLS/SSL, autenticação por usuário/senha |
| **🗄️ Persistência** | Room (SQLite) com migrations |
| **🎤 Voz** | Android SpeechRecognizer + processamento NLP local |
| **🤖 IA** | Integração Google Gemini AI Studio |
| **📊 Testes** | Roborazzi (screenshots), Robolectric, JUnit |

---

## Screenshots

| Splash / Dashboard | Métricas / Telemetria | Config. Dispositivo | Comando de Voz |
|---|---|---|---|
| Anel neon com logo IoT | Gráficos animados e gauges | Cadastro de widgets | Reconhecimento de voz |

---

## Funcionalidades

### 📡 Conexão MQTT
- Conexão direta via socket TCP (implementação raw do protocolo MQTT — sem bibliotecas externas)
- Suporte a MQTT **v3.1**, **v3.1.1** e **v5.0**
- **TLS/SSL** usando `SSLSocketFactory` do Android
- Keep-alive configurável com PINGREQ automático
- Reconexão automática com backoff exponencial (1s → 60s)
- Suporte a autenticação (usuário/senha)
- Subscribe automático em tópicos ao conectar
- Feedback visual com indicador LIVE / OFF / ...

### 🎛️ Dashboard Customizável
- Widgets do tipo **switch** (liga/desliga) e **comando único**
- Grid adaptável com tamanhos compacto e largo
- Imagens customizadas por estado (ON / OFF) com seletor de galeria
- Preview ao vivo do widget antes de salvar
- Modo de exclusão com toque para remover

### 📊 Telemetria em Tempo Real
- Gráficos de linha com **curvas bezier suavizadas**
- Área gradiente preenchida sob a curva
- Múltiplas fontes simultâneas com cores configuráveis
- Estatísticas: valor atual, mínimo, máximo, contagem de amostras
- Histórico das últimas 30 amostras por fonte

### 🎯 Gauges Neon
- Velocímetros radiais com efeito **neon glow** em 5 camadas
- Tipos: temperatura (°C), umidade (% RH), pressão (hPa), gauge genérico (%)
- Edição e exclusão por toque longo

### 🎤 Comandos de Voz
- Reconhecimento de fala nativo do Android (português)
- Comandos inteligentes: "ligar ar condicionado", "desligar tudo"
- Matching fuzzy por palavras-chave com stop words
- Comandos de sistema: "ativar protocolo alfa", "status da bateria", "sincronizar sensores"
- Feedback visual com status: PRONTO → OUVINDO → PROCESSANDO → SUCESSO/ERRO

### 🔧 Offline First / Simulação
- Modo de simulação automática quando desconectado
- Dados sintéticos de temperatura, umidade, latência e armazenamento
- Toda a interface funciona offline com dados simulados

### 📱 UI Cyberpunk
- Tema escuro com paleta neon (ciano, verde limão, magenta)
- Efeitos de brilho (glow) em botões, gauges e gráficos
- Splash animado com anéis pulsantes e gradiente radial
- Cartões com bordas brilhantes semi-transparentes (GlassCard)
- Logo IoT customizado

---

## Arquitetura

```
com.example/
├── MainActivity.kt          # Tela principal, navegação, UI
├── data/
│   ├── Database.kt          # Room: entidades, DAOs, migrations
│   ├── MqttEngine.kt        # Motor MQTT raw socket (conexão, publish, subscribe)
│   ├── NexusRepository.kt   # Camada de repositório (abstração do Room)
│   └── VoiceCommandManager.kt # Processamento de comandos de voz
├── ui/
│   ├── NexusViewModel.kt    # ViewModel principal (estados + lógica de negócio)
│   ├── UiComponents.kt      # Componentes reutilizáveis (GlassCard, NeonGauge, gráfico)
│   └── theme/
│       ├── Color.kt         # Paleta de cores Material 3
│       ├── Theme.kt         # Tema dinâmico do sistema
│       └── Type.kt          # Tipografia
```

### Fluxo de Dados

```
┌─────────────┐     ┌──────────────┐     ┌───────────────┐
│  MQTT Broker │◄───►│  MqttEngine  │────►│ NexusViewModel │
│  (TCP/TLS)   │     │  (raw socket)│     │ (StateFlow)    │
└─────────────┘     └──────┬───────┘     └───────┬───────┘
                           │                     │
                           ▼                     ▼
                    ┌──────────────┐     ┌───────────────┐
                    │  Room/SQLite │     │  Compose UI   │
                    │  (NexusRepo) │     │  (Observação)  │
                    └──────────────┘     └───────────────┘
```

### Banco de Dados (Room)

| Tabela | Descrição |
|--------|-----------|
| `broker_configurations` | Configuração do broker MQTT |
| `mqtt_subscriptions` | Tópicos inscritos ativamente |
| `mqtt_message_logs` | Log de mensagens recebidas |
| `telemetry_sources` | Fontes de telemetria com cor personalizada |
| `dashboard_widgets` | Widgets da grid (switch, comando, gauge) |

---

## Começando

### Pré-requisitos

- **Android Studio** (versão mais recente)
- **JDK 21** (Temurin recomendado)
- **Android SDK** 36+

### Build

```bash
# Clonar
git clone https://github.com/seu-usuario/seu-repo.git
cd MQTT-Dashboard-GoogleStudio

# Build de debug
./gradlew assembleDebug
```

APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Configuração

1. Crie um arquivo `.env` na raiz do projeto (copie de `.env.example`)
2. Configure sua chave da API Gemini (se aplicável):
   ```
   GEMINI_API_KEY=sua_chave_aqui
   ```
3. Abra o app e toque na **engrenagem** no topo para configurar o broker:
   - Host: `test.mosquitto.org` (ou seu broker)
   - Porta: `1883` (ou `8883` para TLS)
   - Client ID: qualquer nome único

### MQTT sem dependências externas

O motor MQTT (`MqttEngine.kt`) implementa o protocolo **diretamente sobre sockets TCP**, sem usar bibliotecas como Eclipse Paho. Isso garante:
- **Tamanho de APK reduzido** (sem dependências pesadas)
- **Controle total** sobre o ciclo de vida da conexão
- **Suporte a MQTT v5.0** com campos de propriedades
- **Código educacional** para entender o protocolo

---

## Estrutura do Projeto

```
MQTT-Dashboard-GoogleStudio/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/    # Código fonte
│   │   │   ├── res/                 # Recursos (imagens, temas, strings)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                    # Testes unitários + Roborazzi
│   │   └── androidTest/             # Testes instrumentados
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml          # Catálogo de versões
├── build.gradle.kts                # Build raiz
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
└── .env.example
```

---

## Dependências Principais

| Biblioteca | Uso |
|------------|-----|
| Jetpack Compose + BOM | UI declarativa |
| Material 3 | Design system + ícones |
| Room + KSP | Persistência SQLite |
| OkHttp + Moshi | HTTP + JSON (futuras integrações) |
| Retrofit | API REST |
| Coroutines + StateFlow | Reatividade |
| Roborazzi | Testes de screenshot |
| Robolectric | Testes unitários com Android |
| Secrets Gradle Plugin | Gerenciamento de chaves via `.env` |

---

## Comandos de Voz Disponíveis

| Comando | Ação |
|---------|------|
| "Ligar [nome do widget]" | Publica payload ON no tópico do widget |
| "Desligar [nome do widget]" | Publica payload OFF no tópico do widget |
| "Ligar tudo" / "Desligar tudo" | Comando broadcast para todos os widgets |
| "Ativar Protocolo Alfa" | Publica `ALFA_ON` em `system/protocol` |
| "Status da bateria" | Requisita status em `system/battery` |
| "Sincronizar sensores" | Dispara sincronização em `sensor/sync` |
| "Iluminação" / "Luz" | Alterna `switch/power_01` |

---

## Variáveis de Ambiente

| Variável | Descrição | Obrigatório |
|----------|-----------|:-----------:|
| `GEMINI_API_KEY` | Chave da API Gemini AI | ✅ |
| `KEYSTORE_PATH` | Caminho do keystore para release | Apenas release |
| `STORE_PASSWORD` | Senha do keystore | Apenas release |
| `KEY_PASSWORD` | Senha da chave | Apenas release |

---

## Solução de Problemas

### Conexão MQTT falha
- Verifique se o broker está acessível (ping)
- Porta 1883 requer `usesCleartextTraffic=true` (já configurado)
- Para brokers remotos, verifique firewall e conectividade
- Ative o log: `adb logcat -s MqttEngine NexusViewModel`

### Android 14+ (API 34)
App configurado com `usesCleartextTraffic=true` no manifest para permitir conexões não-TLS.

### Build lento
Edite `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx512m -XX:MaxMetaspaceSize=256m
org.gradle.parallel=false
```

---

## Testes

```bash
# Testes unitários
./gradlew test

# Testes instrumentados (requer emulador/dispositivo)
./gradlew connectedAndroidTest
```

O projeto usa **Roborazzi** para testes de screenshot com previews do Compose.

---

## Roadmap

- [ ] Widgets do tipo slider e seletor de cores
- [ ] Temas customizáveis (claro/escuro)
- [ ] Suporte a múltiplos brokers simultâneos
- [ ] Backup/Restore da configuração em JSON
- [ ] Integração com Google Home / SmartThings
- [ ] Dashboard de logs com busca
- [ ] Modo escuro dinâmico
- [ ] Suporte a WebSocket MQTT
- [ ] Automações condicionais (se sensor X > Y, ligar Z)

---

## Licença

```
MIT License

Copyright (c) 2025 RobsonBrasil

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```

---

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

1. Fork o projeto
2. Crie sua branch de feature (`git checkout -b feat/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feat/nova-funcionalidade`)
5. Abra um Pull Request

---

<div align="center">
  <p>Feito com ☕ e Kotlin</p>
  <p>
    <a href="https://ai.studio/apps/e7e482cc-307d-42cc-81be-821482cd712a">Google AI Studio</a> ·
    <a href="INSTRUCOES_BUILD.md">Instruções de Build</a>
  </p>
</div>
