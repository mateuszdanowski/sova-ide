package edu.mimuw.sovaide.domain.plugin;

import edu.mimuw.sovaide.domain.repository.ProjectRepository;

public interface PluginSova {
    void execute(ProjectRepository repository);
}
