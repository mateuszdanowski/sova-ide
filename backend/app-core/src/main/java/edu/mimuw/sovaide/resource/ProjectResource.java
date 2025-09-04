package edu.mimuw.sovaide.resource;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.plugin.PluginDTO;
import edu.mimuw.sovaide.plugin.PluginManager;
import edu.mimuw.sovaide.service.ProjectService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectResource {
	private final ProjectService projectService;
	private final PluginManager pluginManager;

	@PostMapping
	public ResponseEntity<Project> createProject(@RequestBody Project project) {
		return ResponseEntity.created(URI.create("/projects/projectID")).body(projectService.createProject(project));
	}

	@GetMapping
	public ResponseEntity<List<Project>> getProjects() {
		return ResponseEntity.ok().body(projectService.getAllProjects());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getProject(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(projectService.getProject(id));
	}

	@GetMapping("/{id}/plugins")
	public ResponseEntity<List<PluginDTO>> getProjectPlugins(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(pluginManager.getPlugins(id));
	}

	@PostMapping("/{id}/plugins/execute-with-file")
	public ResponseEntity<Void> executePluginWithFile(
			@PathVariable(value = "id") String projectId,
			@RequestParam("pluginName") String pluginName,
			@RequestParam("file") MultipartFile file
	) {
		System.out.println("Executing plugin " + pluginName + " for project " + projectId + " with file upload");
		pluginManager.executeWithFile(projectId, pluginName, file);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/plugins/execute")
	public ResponseEntity<Void> executePlugin(
			@PathVariable(value = "id") String projectId,
			@RequestParam("pluginName") String pluginName,
			@RequestBody Map<String, String> properties
	) {
		System.out.println("Executing plugin " + pluginName + " for project " + projectId + " with properties " + properties);
		pluginManager.execute(projectId, pluginName, properties);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProject(@PathVariable("id") String id) {
		projectService.deleteProject(id);
		return ResponseEntity.noContent().build();
	}
}
