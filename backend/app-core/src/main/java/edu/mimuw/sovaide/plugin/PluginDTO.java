package edu.mimuw.sovaide.plugin;

import java.util.List;

import edu.mimuw.sovaide.domain.plugin.PluginResult;

public record PluginDTO(
		String name,
		String type,
		boolean acceptingFile,
		PluginResult result,
		boolean executable,
		boolean viewable,
		List<String> stringInputs) {
}
