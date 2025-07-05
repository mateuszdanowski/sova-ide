package edu.mimuw.sovaide.service;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import edu.mimuw.sovaide.domain.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

	private final TaskExecutor taskExecutor;

	private final JarParseService jarParseService;

	private final PluginExecutor pluginExecutor;

	public void analyzeProjectAsync(Project project) {
		taskExecutor.execute(() -> {
			log.info("Parsing into neo4j started for project: {}", project.getId());
//			jarParseService.parse(project);
//			log.info("Parsing into neo4j finished for project: {}", project.getId());
//			pluginExecutor.executeAll(project.getId());
		});
	}
}
