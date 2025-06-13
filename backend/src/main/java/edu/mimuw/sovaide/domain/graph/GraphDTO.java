package edu.mimuw.sovaide.domain.graph;

import java.util.Set;

public record GraphDTO(
		Set<VertexDTO> nodes,
		Set<EdgeDTO> links
) {
}
