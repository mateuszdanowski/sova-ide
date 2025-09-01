package edu.mimuw.sovaide.domain.plugin;

import lombok.Builder;

/**
 * Represents the result of a plugin execution, including project ID, plugin name,
 * and GUI component data with instructions on displaying results.
 */
@Builder
public record PluginResult(
		String projectId,
		String pluginName,
		GuiComponentData guiComponentData) {
}
