package edu.mimuw.sovaide.plugin;

import java.util.List;

import edu.mimuw.sovaide.domain.plugin.PluginResult;
import edu.mimuw.sovaide.domain.plugin.PluginType;

public record PluginDTO(
		String name,
		PluginType type,
		boolean acceptingFile,
		PluginResult result,
		boolean executable,
		boolean viewable,
		List<String> stringInputs) {
}
