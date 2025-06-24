package edu.mimuw.sovaide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.mimuw.sovaide.domain.plugin.PluginSova;
import edu.mimuw.sovaide.domain.repository.ProjectRepository;
import edu.mimuw.sovaide.plugin.PluginLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PluginExecutor {

	private final PluginLoader pluginLoader;

	private final ProjectRepository repository;

	public void executeAll() {
		List<PluginSova> plugins = pluginLoader.getLoadedPlugins();
		log.info("Executing plugins: {}", plugins);

		for (PluginSova plugin : plugins) {
			try {
				plugin.execute(repository);
				log.info("Successfully executed plugin: {}", plugin.getClass().getName());
			} catch (Exception e) {
				log.error("Error executing plugin {}: {}",
						plugin.getClass().getName(), e.getMessage(), e);
			}
		}
	}
}
