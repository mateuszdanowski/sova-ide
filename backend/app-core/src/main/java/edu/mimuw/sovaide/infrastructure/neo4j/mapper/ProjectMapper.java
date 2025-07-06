package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jProject;

public class ProjectMapper {
    public static Project toDomain(Neo4jProject neo4jProject) {
        if (neo4jProject == null) return null;
        Project project = new Project();
        project.setId(neo4jProject.getId());
        project.setName(neo4jProject.getName());
        project.setFiles(
            neo4jProject.getFiles() == null ? null :
                neo4jProject.getFiles().stream()
                    .map(FileMapper::toDomain)
                    .toList()
        );
		project.setResults(
			neo4jProject.getResults() == null ? null :
				neo4jProject.getResults().stream()
					.map(PluginResultMapper::toDomain)
					.toList()
		);
        return project;
    }

    public static Neo4jProject fromDomain(Project project) {
        if (project == null) return null;
        return Neo4jProject.builder()
            .id(project.getId())
            .name(project.getName())
            .files(
                project.getFiles() == null ? null :
                    project.getFiles().stream()
                        .map(FileMapper::fromDomain)
                        .toList()
            )
			.results(
				project.getResults() == null ? null :
					project.getResults().stream()
						.map(PluginResultMapper::fromDomain)
						.toList()
			)
            .build();
    }
}
