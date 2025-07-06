package edu.mimuw.sovaide.domain.plugin;

import lombok.Builder;

@Builder
public record PluginResult(
		String projectId,
		String pluginName,
		GuiComponentData guiComponentData) {
}
