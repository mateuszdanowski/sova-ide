package edu.mimuw.sovaide.resource;

import static edu.mimuw.sovaide.constant.Constant.FILE_DIRECTORY;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.mimuw.sovaide.domain.Project;
import edu.mimuw.sovaide.domain.graph.GraphDTO;
import edu.mimuw.sovaide.service.ProjectService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectResource {
	private final ProjectService projectService;

	@PostMapping
	public ResponseEntity<Project> createProject(@RequestBody Project project) {
		return ResponseEntity.created(URI.create("/projects/projectID")).body(projectService.createProject(project));
	}

	@GetMapping
	public ResponseEntity<Page<Project>> getProjects(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "12") int size) {
		return ResponseEntity.ok().body(projectService.getAllProjects(page, size));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getProject(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(projectService.getProject(id));
	}

	@GetMapping("/{id}/details")
	public ResponseEntity<GraphDTO> getProjectDetails(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(projectService.getProjectDetails(id));
	}

	@GetMapping("/{id}/packages-graph")
	public ResponseEntity<GraphDTO> getPackagesGraph(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(projectService.getPackagesGraph(id));
	}

	@GetMapping("/{id}/package-imports-graph")
	public ResponseEntity<GraphDTO> getPackageImportsGraph(@PathVariable(value = "id") String id) {
		return ResponseEntity.ok().body(projectService.getPackageImportsGraph(id));
	}

	@PutMapping("/file")
	public ResponseEntity<String> uploadFile(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
		System.out.println("Saving file for project " + id);
		return ResponseEntity.ok().body(projectService.uploadFile(id, file));
	}

	@GetMapping(path = "/file/{filename}", produces = { "application/java-archive", "application/zip" })
	public byte[] getFile(@PathVariable("filename") String filename) throws IOException {
		return Files.readAllBytes(Paths.get(FILE_DIRECTORY + filename));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProject(@PathVariable("id") String id) {
		projectService.deleteProject(id);
		return ResponseEntity.noContent().build();
	}
}
