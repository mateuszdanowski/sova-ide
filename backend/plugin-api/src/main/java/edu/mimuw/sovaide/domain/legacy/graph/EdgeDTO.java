package edu.mimuw.sovaide.domain.legacy.graph;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EdgeDTO(String source, String target, String kind) {
}
