package edu.mimuw.sovaide.domain.plugin;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;

public interface PluginSova {
	String getName();

	String getType();

	boolean isAcceptingFile();

    void execute(String projectId, ProjectRepository repository, GraphDBFacade graphDBFacade, String fileUrl);
}
