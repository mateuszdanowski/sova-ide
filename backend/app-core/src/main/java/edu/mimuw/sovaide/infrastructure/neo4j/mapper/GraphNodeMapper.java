package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import java.util.Map;

import org.neo4j.driver.types.Node;

import edu.mimuw.sovaide.domain.graph.GraphNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphNodeMapper {

	public static GraphNode from(Node node) {
		String id = String.valueOf(node.id());
		String label = node.labels().iterator().hasNext() ? node.labels().iterator().next() : "";
		Map<String, Object> properties = node.asMap();
		return new GraphNode(id, label, properties);
	}
}
