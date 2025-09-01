package edu.mimuw.sovaide.domain.graph;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a node in the graph database, including its label and properties.
 */
@AllArgsConstructor
@Getter
public class GraphNode {
	private String id;
	private String label;
	private Map<String, Object> properties;
}
