package edu.mimuw.sovaide.service;

import static edu.mimuw.sovaide.constant.Constant.FILE_DIRECTORY;
import static edu.mimuw.sovaide.constant.Constant.getLocalFilePath;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.domain.plugin.DatabaseInterfaces;
import edu.mimuw.sovaide.domain.plugin.PluginResult;
import edu.mimuw.sovaide.domain.plugin.PluginSova;
import edu.mimuw.sovaide.plugin.PluginLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PluginExecutor {

	private final PluginLoader pluginLoader;

	private final ProjectRepository repository;

	private final ProjectService projectService;

	private final GraphDBFacade graphDBFacade;

	public void executeWithFile(String projectId, String pluginName, MultipartFile file) {
		PluginSova plugin = pluginLoader.getLoadedPlugins().stream()
				.filter(p -> p.getName().equals(pluginName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginName));

		String fileUrl = fileFunction.apply(projectId, file);
		String localFilePath = getLocalFilePath(projectId, fileUrl);

		log.info("Executing plugin {} with file {} for project {}", pluginName, fileUrl, projectId);
		PluginResult result = plugin.execute(projectId, new DatabaseInterfaces(repository, graphDBFacade), localFilePath);
		log.info("Plugin {} executed successfully with file {}", pluginName, fileUrl);

		if (result != null) {
			projectService.savePluginResult(result);
		}
	}

	public void execute(String projectId, String pluginName) {
		PluginSova plugin = pluginLoader.getLoadedPlugins().stream()
				.filter(p -> p.getName().equals(pluginName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginName));

		log.info("Executing plugin {} for project {}", pluginName, projectId);
		PluginResult result = plugin.execute(projectId, new DatabaseInterfaces(repository, graphDBFacade), null);
		log.info("Plugin {} executed successfully for project {}", pluginName, projectId);

		if (result != null) {
			projectService.savePluginResult(result);
		}
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
