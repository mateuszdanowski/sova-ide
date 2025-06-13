package edu.mimuw.sovaide.domain.graph;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EdgeDTO(String source, String target, @Nullable String kind) {
}
