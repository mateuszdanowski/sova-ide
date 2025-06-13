package edu.mimuw.sovaide.service;

import static edu.mimuw.sovaide.constant.Constant.getLocalFilePath;
import static java.util.function.Predicate.not;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import edu.mimuw.sovaide.domain.legacy.JavaClass;
import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// todo separate to a plugin
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

	private final TaskExecutor taskExecutor;

	private final ProjectRepository repository;

	private final GraphBuildingService graphBuildingService;

	public void analyzeProjectAsync(Project project) {
		taskExecutor.execute(() -> {
			log.info("Running background analysis for project: {}", project.getId());
			long classCount = countMetric(this::countClasses, project);

			long importsCount = countMetric(this::countImports, project);

			List<JavaClass> classes = graphBuildingService.fetchClasses(project);

			// create a graph from the classes
			graphBuildingService.buildGraph(project.getId(), classes);

			Long avgLinesOfCode = countAvgLinesOfCode(classes);

			project.setNoOfClasses(classCount);
			project.setNoOfImports(importsCount);
			project.setAvgLinesOfCode(avgLinesOfCode);
			project.setStatus("ANALYZED");
			repository.save(project);

			log.info("Analysis finished for project: {}", project.getId());
		});
	}

	private Long countAvgLinesOfCode(List<JavaClass> classes) {
		return (long) (classes.stream()
						.mapToInt(c -> c.content().split("\n").length)
						.reduce(0, Integer::sum) / classes.size());
	}

	private long countMetric(Function<CompilationUnit, Long> metricFunction, Project project) {
		String localFilePath = getLocalFilePath(project.getId(), project.getFileUrl());
		long result = 0;

		try (JarFile jarFile = new JarFile(localFilePath)) {
			log.info("Analysing JAR file: {}", localFilePath);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".java")) {
					log.info("Java entry found: {}", entry.getName());
					try (InputStream inputStream = jarFile.getInputStream(entry)) {
						try {
							CompilationUnit r = StaticJavaParser.parse(inputStream);
							result += metricFunction.apply(r);
						} catch (ParseProblemException e) {
							log.error("Error when parsing {}: {}", entryName, e.getMessage());
						}
					}
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return result;
	}

	private long countImports(CompilationUnit cu) {
		return cu.findAll(ImportDeclaration.class).stream()
				.filter(not(ImportDeclaration::isAsterisk))
				.filter(not(ImportDeclaration::isStatic))
				.count();
	}

	private long countClasses(CompilationUnit cu) {
		return cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.map(ClassOrInterfaceDeclaration::getFullyQualifiedName)
				.filter(Optional::isPresent)
				.count();
	}
}
