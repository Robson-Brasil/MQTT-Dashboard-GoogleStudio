import json
from pathlib import Path
g = json.loads(Path('graphify-out/graph.json').read_text(encoding='utf-8'))
nodes = g.get('nodes', [])
edges = g.get('edges', [])
print(f'Graph: {len(nodes)} nodes, {len(edges)} edges')

keywords = ['widget', 'grid', 'dispositivo', 'aparelho', 'device', 'imagem', 'imagen', 'image', 'salvar', 'save', 'custom', 'botao', 'button']
for k in keywords:
    matches = [n for n in nodes if k.lower() in n.get('label','').lower() or k.lower() in n.get('id','').lower()]
    if matches:
        print(f'\n--- {k} ({len(matches)} matches) ---')
        for m in matches[:8]:
            print(f'  {m.get("id","")} - {m.get("label","")} - com:{m.get("community","?")}')
