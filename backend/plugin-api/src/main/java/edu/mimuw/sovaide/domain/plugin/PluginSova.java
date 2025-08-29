package edu.mimuw.sovaide.domain.plugin;

import java.util.List;

public interface PluginSova {
	String getName();

	String getType();

	boolean isAcceptingFile();

	default List<String> getStringInputs() {
		return List.of();
	}

    PluginResult execute(String projectId, DatabaseInterfaces databaseInterfaces, UserInput userInput);
}
