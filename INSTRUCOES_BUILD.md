# Instruções para Build e Instalação do MQTT Dashboard

## Correções Aplicadas

O código já foi corrigido com 4 alterações para resolver o problema de conexão MQTT:

| # | Bug | Arquivo | Linha | Descrição |
|---|-----|---------|-------|-----------|
| 1 | Android 14+ bloqueia conexão HTTP (porta 1883) | `app/src/main/AndroidManifest.xml` | 16 | Adicionado `android:usesCleartextTraffic="true"` |
| 2 | Broker MQTT v5.0 rejeita CONNECT sem campo Properties | `app/src/main/java/com/example/data/MqttEngine.kt` | 267-270 | Adicionado byte 0x00 (Properties vazias) após Keep Alive |
| 3 | Erro de conexão engolido sem feedback pro usuário | `app/src/main/java/com/example/data/MqttEngine.kt` | 36-37, 47, 123 | Adicionado `connectionError: StateFlow<String?>` |
| 4 | Erro de digitação `outSteam` causa crash no publish | `app/src/main/java/com/example/data/MqttEngine.kt` | 174 | Renomeado para `outStream` |

O erro de conexão agora aparece como um Toast na tela principal.

---

## Pré-requisitos

- **Windows 10** (64 bits)
- **Conexão com internet** para baixar as ferramentas (apenas na primeira vez)
- ~2 GB de espaço livre em disco
- **Cabo USB** para conectar o celular Android ao PC

---

## Passo 1: Instalar o JDK 21

Abra o **PowerShell como Administrador** e execute:

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

Feche e reabra o PowerShell para o PATH atualizar.

Verifique a instalação:

```powershell
java -version
```

A saída deve mostrar algo como `openjdk version "21.0.x"`.

---

## Passo 2: Instalar o Android SDK

Ainda no PowerShell como Administrador:

```powershell
# Criar diretório do SDK
mkdir C:\Android -Force

# Baixar as command-line tools
Invoke-WebRequest -Uri "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip" -OutFile "$env:TEMP\cmdline-tools.zip"

# Extrair
Expand-Archive -Path "$env:TEMP\cmdline-tools.zip" -DestinationPath "$env:TEMP\cmdline-tools-tmp" -Force
mkdir C:\Android\cmdline-tools\latest -Force
Move-Item "$env:TEMP\cmdline-tools-tmp\cmdline-tools\*" "C:\Android\cmdline-tools\latest\" -Force

# Aceitar licenças
C:\Android\cmdline-tools\latest\bin\sdkmanager.bat --sdk_root=C:\Android --licenses
# Digite "y" para aceitar todas

# Instalar os pacotes necessários
C:\Android\cmdline-tools\latest\bin\sdkmanager.bat --sdk_root=C:\Android "platforms;android-36" "build-tools;36.0.0" "platform-tools"

# Configurar variável de ambiente permanente
[Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Android", "User")
```

---

## Passo 3: Buildar o APK

No diretório do projeto (`C:\Users\RobsonBrasil\Desktop\MQTT-Dashboard-GoogleStudio`):

```powershell
# Definir JAVA_HOME (ajuste o nome da pasta se necessário)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.2.13-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Build de depuração
.\gradlew.bat assembleDebug
```

O APK será gerado em: `app\build\outputs\apk\debug\app-debug.apk`

---

## Passo 4: Instalar no Celular via USB

1. No celular, vá em **Configurações > Sobre o telefone** e toque 7 vezes em "Número da versão" para ativar as Opções do Desenvolvedor
2. Vá em **Configurações > Opções do Desenvolvedor** e ative:
   - **Depuração USB**
3. Conecte o celular ao PC via cabo USB
4. Autorize a depuração quando a janela aparecer no celular

No PowerShell:

```powershell
# Verificar se o celular foi detectado
C:\Android\platform-tools\adb devices

# Instalar o APK
C:\Android\platform-tools\adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## Passo 5: Testar e Monitorar

Com o app instalado e aberto no celular:

```powershell
# Ver logs em tempo real (filtrados pelo MQTT Engine)
C:\Android\platform-tools\adb logcat -s MqttEngine NexusViewModel
```

Isso mostra todas as mensagens de log do motor MQTT e do ViewModel, incluindo erros de conexão.

Para salvar os logs em um arquivo:

```powershell
C:\Android\platform-tools\adb logcat -s MqttEngine NexusViewModel > mqtt_logs.txt
```

---

## Configuração Ideal para PC Fraco

Edite o arquivo `gradle.properties` na raiz do projeto com:

```properties
org.gradle.jvmargs=-Xmx512m -XX:MaxMetaspaceSize=256m
org.gradle.parallel=false
org.gradle.daemon=false
```

Isso reduz o consumo de RAM durante o build de 4 GB para ~800 MB. O build será mais lento, mas não travará o computador.

---

## Configuração do Broker no App

Ao abrir o app no celular, toque no ícone de **engrenagem** para configurar:

| Campo | Exemplo |
|-------|---------|
| Host | `test.mosquitto.org` |
| Porta | `1883` (ou `8883` para TLS) |
| TLS | Desligado para porta 1883 |
| Client ID | `dashboard_android` (pode ser qualquer nome) |
| MQTT Version | `MQTT v3.1.1` (recomendado) |

Use `test.mosquitto.org:1883` para testar — é um broker público que não precisa de login.

---

## Solução de Problemas

### "A operação solicitada não pôde ser concluída devido a uma limitação do sistema"

Isso acontece se o PowerShell não foi aberto como **Administrador**. Feche e abra como Administrador.

### Gradle build muito lento ou trava

Abra `gradle.properties` e confirme que `org.gradle.jvmargs` está com `-Xmx512m`. Feche outros programas (navegador, etc.) antes de buildar.

### APK não instala no celular

```powershell
# Se já existe uma versão instalada, use -r para substituir:
C:\Android\platform-tools\adb install -r app\build\outputs\apk\debug\app-debug.apk

# Se der erro de "INSTALL_FAILED_UPDATE_INCOMPATIBLE", desinstale primeiro no celular:
C:\Android\platform-tools\adb uninstall com.example.app
```

### Conexão MQTT continua falhando

```powershell
# Capture o log completo do MQTT
C:\Android\platform-tools\adb logcat -s MqttEngine NexusViewModel > mqtt_debug.txt
```

Compartilhe o arquivo `mqtt_debug.txt` para análise.
