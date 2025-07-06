package edu.mimuw.sovaide.domain.plugin;

import java.util.Map;

public record GuiComponentData(String componentType,
							   Map<String, Object> data,
							   Map<String, Object> config) {
}
