package edu.mimuw.sovaide.plugin;

import static edu.mimuw.sovaide.constant.Constant.FILE_DIRECTORY;
import static edu.mimuw.sovaide.constant.Constant.getLocalFilePath;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.domain.plugin.DatabaseInterfaces;
import edu.mimuw.sovaide.domain.plugin.PluginResult;
import edu.mimuw.sovaide.domain.plugin.PluginSova;
import edu.mimuw.sovaide.domain.plugin.UserInput;
import edu.mimuw.sovaide.service.ProjectService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class PluginManager {

	private static final String PLUGIN_DIR = "plugins";

	private final List<PluginSova> loadedPlugins = new ArrayList<>();

	private final ProjectRepository repository;
	private final ProjectService projectService;
	private final GraphDBFacade graphDBFacade;

	@PostConstruct
	public void loadPlugins() {
		File pluginDir = new File(PLUGIN_DIR);
		if (!pluginDir.exists() || !pluginDir.isDirectory()) {
			System.out.println("No plugins directory found.");
			return;
		}
		File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
		if (jars == null) return;
		for (File jar : jars) {
			try {
				loadPluginFromJar(jar);
			} catch (Exception e) {
				log.error("Failed to load plugin from {}: {}", jar.getName(), e.getMessage());
			}
		}
	}

	private void loadPluginFromJar(File jarFile) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		try (JarFile jar = new JarFile(jarFile)) {
			URL[] urls = { new URL("jar:file:" + jarFile.getAbsolutePath() + "!/") };
			try (URLClassLoader cl = new URLClassLoader(urls, this.getClass().getClassLoader())) {
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName().replace('/', '.').replace(".class", "");
						Class<?> clazz = cl.loadClass(className);
						if (PluginSova.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
							PluginSova plugin = (PluginSova) clazz.getDeclaredConstructor().newInstance();
							loadedPlugins.add(plugin);
							log.info("Loaded plugin: {}", className);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void executeWithFile(String projectId, String pluginName, MultipartFile file) {
		PluginSova plugin = loadedPlugins.stream()
				.filter(p -> p.getName().equals(pluginName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginName));

		String fileUrl = fileFunction.apply(projectId, file);
		String localFilePath = getLocalFilePath(projectId, fileUrl);

		log.info("Executing plugin {} with file {} for project {}", pluginName, fileUrl, projectId);
		PluginResult result = plugin.execute(projectId, new DatabaseInterfaces(repository, graphDBFacade), new UserInput(localFilePath, Map.of()));
		log.info("Plugin {} executed successfully with file {}", pluginName, fileUrl);

		if (result != null) {
			projectService.savePluginResult(result);
		}
	}

	public void execute(String projectId, String pluginName, Map<String, String> properties) {
		PluginSova plugin = loadedPlugins.stream()
				.filter(p -> p.getName().equals(pluginName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginName));

		log.info("Executing plugin {} for project {}", pluginName, projectId);
		PluginResult result = plugin.execute(projectId, new DatabaseInterfaces(repository, graphDBFacade),
				new UserInput(null, properties));
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
		loadedPlugins.forEach(pluginSova -> {
			String pluginName = pluginSova.getName();
			pluginMap.put(pluginName, new PluginDTO(
					pluginName,
					pluginSova.getType(),
					pluginSova.isAcceptingFile(),
					null, // no result yet
					true, // executable
					false, // not viewable yet
					pluginSova.getStringInputs()
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
							true, // now viewable
							existingPlugin.stringInputs()
					));
				} else {
					// Plugin has result but is not loaded - only viewable
					pluginMap.put(pluginName, new PluginDTO(
							pluginName,
							"UNKNOWN", // type unknown for unloaded plugins
							false, // accepting file unknown for unloaded plugins
							result,
							false, // not executable
							true, // viewable
							List.of()
					));
				}
			});
		}

		return pluginMap.values().stream().toList();
	}
}
