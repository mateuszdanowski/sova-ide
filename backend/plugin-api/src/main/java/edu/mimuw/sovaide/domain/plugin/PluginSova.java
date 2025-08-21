package edu.mimuw.sovaide.domain.plugin;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;

public interface PluginSova {
	String getName();

	String getType();

	boolean isAcceptingFile();

    PluginResult execute(String projectId, DatabaseInterfaces databaseInterfaces, String fileUrl);
}
