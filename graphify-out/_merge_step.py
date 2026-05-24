import sys, json
from graphify.build import build_from_json
from graphify.export import to_json
from networkx.readwrite import json_graph
import networkx as nx
from pathlib import Path

# Load existing graph
existing_data = json.loads(Path('graphify-out/graph.json').read_text())
G_existing = json_graph.node_link_graph(existing_data, edges='links')
old_node_count = G_existing.number_of_nodes()
old_edge_count = G_existing.number_of_edges()
print(f'Existing graph: {old_node_count} nodes, {old_edge_count} edges')

# Load new extraction
new_extraction = json.loads(Path('graphify-out/.graphify_extract.json').read_text())
G_new = build_from_json(new_extraction)
print(f'New extraction: {G_new.number_of_nodes()} nodes, {G_new.number_of_edges()} edges')

# Merge: new nodes/edges into existing graph
G_existing.update(G_new)
print(f'Merged: {G_existing.number_of_nodes()} nodes, {G_existing.number_of_edges()} edges')

# Save merged graph back
merged_data = json_graph.node_link_data(G_existing)
Path('graphify-out/_merged_graph.json').write_text(json.dumps(merged_data, indent=2))
Path('graphify-out/graph.json').write_text(json.dumps(merged_data, indent=2))
print(f'Saved merged graph to graph.json')
