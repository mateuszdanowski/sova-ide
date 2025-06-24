package edu.mimuw.sovaide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import edu.mimuw.sovaide.domain.model.Entity;
import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdditionalRelationshipsCreator {

	private final ProjectRepository projectRepository;

	public void execute(Project project) {
		List<Entity> entities = project.getAllEntities();

		entities.forEach(entity -> {
			CompilationUnit unit = StaticJavaParser.parse(entity.getContent());
			if (unit.getTypes().size() != 1) {
				throw new RuntimeException("Expected exactly one declaration in entity object " + entity.getName());
			}
			TypeDeclaration<?> type = unit.getType(0);

			if (type.isClassOrInterfaceDeclaration()) {
				handleImplementedTypes(entity, type.asClassOrInterfaceDeclaration().getImplementedTypes(), project);
			} else if (type.isEnumDeclaration()) {
				handleImplementedTypes(entity, type.asEnumDeclaration().getImplementedTypes(), project);
			} else {
				log.warn("Entity {} is not a class or interface declaration, skipping additional relationships.",
						entity.getName());
			}
		});
//		projectRepository.save(project);
	}

	private void handleImplementedTypes(Entity entity, NodeList<ClassOrInterfaceType> implementedTypes, Project project) {
//		List<Entity> implementedEntities = implementedTypes.stream()
//				.map(implementedType -> {
//					log.info("Processing implemented type: {}", implementedType.getNameAsString());
//					log.info("Processing projectId: {}", project.getId());
//					List<Entity> entities = projectRepository.findNeo4jEntityByNameAndProjectId(
//							implementedType.getNameAsString(), project.getId());
//					if (entities.isEmpty()) {
//						log.error("Warning: Implemented type " + implementedType + " not found in project.");
//						return null;
//					} else if (entities.size() > 1) {
//						log.error("Warning: Multiple entities found for implemented type " + implementedType
//								+ ", using the first one.");
//					}
//					return entities.getFirst();
//				})
//				.filter(Objects::nonNull)
//				.toList();
//		entity.setImplementsEntities(implementedEntities);
//		projectRepository.save(entity);
		log.info("Would update entity {}", entity.getName());
	}
}
