package edu.mimuw.sovaide.domain.graph;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GraphDBFacade {
	// Nodes
	GraphNode createNode(String label, Map<String, Object> properties);
	Optional<GraphNode> updateNode(String id, Map<String, Object> properties);
	Optional<GraphNode> getNodeById(String id);
	List<GraphNode> findNodes(String label, Map<String, Object> filters);
	void deleteAllWithProperty(String propertyName, Object propertyValue);

	// Edges
	GraphEdge createEdge(GraphNode from, GraphNode to, String type, Map<String, Object> properties);
	List<GraphEdge> getEdges(GraphNode node, String type, EdgeDirection direction);

	// Custom query (fallback)
	List<Map<String, Object>> executeCypher(String cypher, Map<String, Object> parameters);
}
