package edu.mimuw.sovaide.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.plugin.PluginDTO;
import edu.mimuw.sovaide.plugin.PluginLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProjectService {
	private final ProjectRepository repository;
	private final PluginLoader pluginLoader;

	public List<Project> getAllProjects() {
		return repository.findAll();
	}

	public Project getProject(String id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
	}

	public List<PluginDTO> getPlugins(String id) {
		return pluginLoader.getLoadedPlugins().stream()
				.map(pluginSova ->
						new PluginDTO(pluginSova.getName(), pluginSova.getType(), pluginSova.isAcceptingFile()))
				.toList();
	}

	public Project createProject(Project project) {
		return repository.save(project);
	}

	public void deleteProject(String id) {
		repository.deleteById(id);
	}
}
