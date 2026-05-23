# Task Plan: Correção da Aba Aparelho (DeviceConfigScreen)

## Problemas
1. Botões "ESCOLHER IMAGEM ON/OFF" não funcionam - só Toast
2. WidgetConfig não tem campos para armazenar imagens customizadas
3. "Salvar Dados" usa valores hardcoded, ignora formulário
4. Não há seletor de tipo de widget na tela
5. Sem biblioteca de loading de imagens (Coil comentado)

## Fases

### Fase 1: Adicionar campos de imagem ao WidgetConfig
- Adicionar `imageOnUri: String` e `imageOffUri: String` ao WidgetConfig (Database.kt)
- Adicionar migration do Room (Database.kt)
- Atualizar NexusRepository conforme necessário
- Atualizar ViewModel

### Fase 2: Implementar image picker real
- Adicionar `rememberLauncherForActivityResult` com `GetContent()` 
- Botões "ESCOLHER IMAGEM ON/OFF" abrem galeria
- Preview das imagens selecionadas
- Armazenar URIs no estado

### Fase 3: Corrigir "Salvar Dados"
- Adicionar seletor de tipo de widget (switch/command)
- Usar campos do formulário no salvamento
- Passar URIs das imagens ao salvar
- Atualizar ViewModel.addCustomWidget()

### Fase 4: Exibir imagens no Grid (Dashboard)
- Atualizar DashboardScreen para mostrar imagens customizadas
- Usar BitmapFactory para carregar de URI

### Fase 5: Build e verificação
- Compilar e verificar

## Decisões
- Usar BitmapFactory (sem dependência extra) para carregar imagens de URI
- Armazenar URI string (simples, funcional para MVP)
- Content URI pode expirar - alternativa aceitável para MVP
