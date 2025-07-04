package edu.mimuw.sovaide.domain.graph;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GraphNode {
	private String id;
	private String label;
	private Map<String, Object> properties;
}
