package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import java.util.Map;

import org.neo4j.driver.types.Relationship;

import edu.mimuw.sovaide.domain.graph.GraphEdge;
import edu.mimuw.sovaide.domain.graph.GraphNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphEdgeMapper {

	public static GraphEdge from(Relationship rel, GraphNode start, GraphNode end) {
		String id = String.valueOf(rel.id());
		String type = rel.type();
		Map<String, Object> properties = rel.asMap();
		return new GraphEdge(id, type, start, end, properties);
	}
}
