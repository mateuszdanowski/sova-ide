package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.Project;
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
            .build();
    }
}
