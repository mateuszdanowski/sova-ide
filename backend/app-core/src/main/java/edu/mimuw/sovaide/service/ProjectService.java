package edu.mimuw.sovaide.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.domain.plugin.PluginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProjectService {
	private final ProjectRepository repository;

	public List<Project> getAllProjects() {
		return repository.findAll();
	}

	public Project getProject(String id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
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
