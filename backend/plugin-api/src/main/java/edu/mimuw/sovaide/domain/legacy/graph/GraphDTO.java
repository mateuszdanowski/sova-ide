package edu.mimuw.sovaide.domain.legacy.graph;

import java.util.Set;

public record GraphDTO(
		Set<VertexDTO> nodes,
		Set<EdgeDTO> links
) {
}
