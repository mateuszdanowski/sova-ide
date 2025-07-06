package edu.mimuw.sovaide.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.domain.plugin.PluginResult;
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

	public List<PluginDTO> getPlugins(String projectId) {
		// all loaded plugins + all results
		// loaded plugins can be executed
		// plugins which have results can be viewed
		Project project = repository.findById(projectId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found"));
		List<PluginResult> pluginResults = project.getResults();

		// Create a map to store unique plugins by name
		Map<String, PluginDTO> pluginMap = new HashMap<>();

		// Add all loaded plugins (these are executable)
		pluginLoader.getLoadedPlugins().forEach(pluginSova -> {
			String pluginName = pluginSova.getName();
			pluginMap.put(pluginName, new PluginDTO(
				pluginName,
				pluginSova.getType(),
				pluginSova.isAcceptingFile(),
				null, // no result yet
				true, // executable
				false // not viewable yet
			));
		});

		// Add/update plugins that have results (these are viewable)
		if (pluginResults != null) {
			pluginResults.forEach(result -> {
				String pluginName = result.pluginName();
				PluginDTO existingPlugin = pluginMap.get(pluginName);

				if (existingPlugin != null) {
					// Update existing plugin to be viewable and include result
					pluginMap.put(pluginName, new PluginDTO(
						existingPlugin.name(),
						existingPlugin.type(),
						existingPlugin.acceptingFile(),
						result,
						existingPlugin.executable(), // keep executable status
						true // now viewable
					));
				} else {
					// Plugin has result but is not loaded - only viewable
					pluginMap.put(pluginName, new PluginDTO(
						pluginName,
						"UNKNOWN", // type unknown for unloaded plugins
						false, // accepting file unknown for unloaded plugins
						result,
						false, // not executable
						true // viewable
					));
				}
			});
		}

		return pluginMap.values().stream().toList();
	}

	public void savePluginResult(PluginResult pluginResult) {
		log.info("Saving {} plugin result for project {}", pluginResult.pluginName() ,pluginResult.projectId());
		String projectId = pluginResult.projectId();
		Project project = getProject(projectId);

		List<PluginResult> existingResults = project.getResults();
		List<PluginResult> newResults = new ArrayList<>();

		// Add all existing results except the one with the same plugin name - we want to overwrite it
		if (existingResults != null) {
			existingResults.stream()
				.filter(result -> !result.pluginName().equals(pluginResult.pluginName()))
				.forEach(newResults::add);
		}
		newResults.add(pluginResult);

		project.setResults(newResults);
		repository.save(project);
	}

	public Project createProject(Project project) {
		return repository.save(project);
	}

	public void deleteProject(String id) {
		repository.deleteById(id);
	}
}
