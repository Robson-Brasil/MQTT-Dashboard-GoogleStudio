# Guia Graphify — MQTT Dashboard

Graphify cria um **grafo de conhecimento** do código pra IA não se perder nos arquivos.

## Comandos essenciais

### Atualizar o grafo depois de mexer no código

```bash
graphify update "."
```

Rápido, só AST (sem custo de API). Roda sempre que você alterar código.

### Reconstruir do zero (se adicionar docs, PDFs, imagens)

```bash
graphify extract "." --backend gemini
```

Tem custo (~$0.03). Só quando adicionar documentação ou arquivos novos que não são código.

### Perguntar algo sobre o código

```bash
graphify query "como funciona o fluxo de telemetria" --graph graphify-out/graph.json
```

A IA usa isso automaticamente, mas pode rodar manualmente também.

### Ver relações entre duas coisas

```bash
graphify path "DashboardScreen" "WidgetConfig" --graph graphify-out/graph.json
```

### Explicar um conceito

```bash
graphify explain "loadWidgetBitmap" --graph graphify-out/graph.json
```

### Ver o relatório completo do projeto

`graphify-out/GRAPH_REPORT.md` — visão geral da arquitetura, nós principais, conexões.

### Visualizar o grafo

Abrir `graphify-out/graph.html` no navegador — grafo interativo clicável.

## Fluxo do dia a dia

1. Coda normalmente
2. Quando for pedir ajuda pra IA, roda `graphify update "."` se tiver mudado código
3. Pronto — a IA já usa o grafo automaticamente

## Se o grafo ficar desatualizado

```bash
graphify extract "." --force --backend gemini
```

Força reconstrução completa, ignorando cache.

graphify export callflow-html
