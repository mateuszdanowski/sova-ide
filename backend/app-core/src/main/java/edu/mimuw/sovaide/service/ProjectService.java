package edu.mimuw.sovaide.service;

import static edu.mimuw.sovaide.constant.Constant.FILE_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProjectService {
	private final ProjectRepository repository;
	private final AnalysisService analysisService;

	public List<Project> getAllProjects() {
		return repository.findAll();
	}

	public Project getProject(String id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
	}
	public Project createProject(Project project) {
		return repository.save(project);
	}

	public void deleteProject(String id) {
		repository.deleteById(id);
	}

	public String uploadFile(String id, MultipartFile file) {
		log.info("Saving file for project {}", id);
		Project project = getProject(id);
		String fileUrl = fileFunction.apply(id, file);
		project.setFileUrl(fileUrl);
		log.info("File saved for project {} under {}", id, fileUrl);

		Project savedProject = repository.save(project);
		analysisService.analyzeProjectAsync(savedProject);

		return fileUrl;
	}

	private final Function<String, String> fileExtension = filename -> Optional.of(filename)
			.filter(name -> name.contains("."))
			.map(name -> name.substring(filename.lastIndexOf(".")))
			.orElse(".jar");

	private final BiFunction<String, MultipartFile, String> fileFunction = (id, file) -> {
		String filename = id + fileExtension.apply(file.getOriginalFilename());
		try {
			Path fileStorageLocation = Paths.get(FILE_DIRECTORY).toAbsolutePath().normalize();
			if (!Files.exists(fileStorageLocation)) {
				Files.createDirectories(fileStorageLocation);
			}
			Files.copy(file.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
			return ServletUriComponentsBuilder
					.fromCurrentContextPath()
					.path("/projects/file/" + filename).toUriString();
		} catch (Exception exception) {
			throw new RuntimeException("Unable to save file");
		}
	};
}
