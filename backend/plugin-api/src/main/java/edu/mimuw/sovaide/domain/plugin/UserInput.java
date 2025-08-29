package edu.mimuw.sovaide.domain.plugin;

import java.util.Map;

// a single file, or a map of string -> string properties
public record UserInput(String fileUrl, Map<String, String> properties) {
}
