package edu.mimuw.sovaide.domain.graph;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GraphEdge {
    private String id;
    private String type;
    private GraphNode startNode;
    private GraphNode endNode;
    private Map<String, Object> properties;
}
