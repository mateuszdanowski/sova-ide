package edu.mimuw.sovaide.domain.plugin;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;

public interface PluginSova {
    void execute(ProjectRepository repository, GraphDBFacade graphDBFacade);
}
