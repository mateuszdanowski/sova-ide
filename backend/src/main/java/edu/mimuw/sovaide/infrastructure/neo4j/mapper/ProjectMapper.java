package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import java.util.stream.Collectors;

import edu.mimuw.sovaide.domain.model.Project;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jProject;

public class ProjectMapper {
    public static Project toDomain(Neo4jProject neo4jProject) {
        if (neo4jProject == null) return null;
        Project project = new Project();
        project.setId(neo4jProject.getId());
        project.setName(neo4jProject.getName());
        project.setFileUrl(neo4jProject.getFileUrl());
        project.setStatus(neo4jProject.getStatus());
        project.setNoOfClasses(neo4jProject.getNoOfClasses());
        project.setNoOfImports(neo4jProject.getNoOfImports());
        project.setAvgLinesOfCode(neo4jProject.getAvgLinesOfCode());
        project.setFiles(
            neo4jProject.getFiles() == null ? null :
                neo4jProject.getFiles().stream()
                    .map(FileMapper::toDomain)
                    .collect(Collectors.toList())
        );
        return project;
    }

    public static Neo4jProject fromDomain(Project project) {
        if (project == null) return null;
        return Neo4jProject.builder()
            .id(project.getId())
            .name(project.getName())
            .language("java")
            .fileUrl(project.getFileUrl())
            .status(project.getStatus())
            .noOfClasses(project.getNoOfClasses())
            .noOfImports(project.getNoOfImports())
            .avgLinesOfCode(project.getAvgLinesOfCode())
            .files(
                project.getFiles() == null ? null :
                    project.getFiles().stream()
                        .map(FileMapper::fromDomain)
                        .collect(Collectors.toList())
            )
            .build();
    }
}
