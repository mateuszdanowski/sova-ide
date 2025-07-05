package edu.mimuw.sovaide.infrastructure.neo4j.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jProject;
import edu.mimuw.sovaide.infrastructure.neo4j.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Neo4jProjectGraphRepository implements ProjectRepository {

	private final SpringDataNeo4jProjectGraphRepository projectRepository;

	private final GraphDBFacade graphDBFacade;

	@Override
	public Project save(Project project) {
		Neo4jProject neo4jProject = ProjectMapper.fromDomain(project);
		Neo4jProject saved = projectRepository.save(neo4jProject);
		return ProjectMapper.toDomain(saved);
	}

	@Override
	public List<Project> findAll() {
		return projectRepository.findAll().stream()
				.map(ProjectMapper::toDomain)
				.toList();
	}

	@Override
	public Optional<Project> findById(String id) {
		return projectRepository.findById(id)
				.map(ProjectMapper::toDomain);
	}

	@Override
	public void deleteById(String id) {
		// first, delete all the nodes and relations associated with the project
		graphDBFacade.deleteAllWithProperty("projectId", id);
		// then delete the project node itself
		projectRepository.deleteById(id);
	}
}
