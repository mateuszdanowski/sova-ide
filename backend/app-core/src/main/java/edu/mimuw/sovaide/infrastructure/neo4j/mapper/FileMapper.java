package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.model.File;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jFile;

public class FileMapper {
    public static File toDomain(Neo4jFile neo4jFile) {
        if (neo4jFile == null) return null;
        File file = new File();
        file.setId(neo4jFile.getId());
        file.setProjectId(neo4jFile.getProjectId());
        file.setPath(neo4jFile.getPath());
        file.setKind(neo4jFile.getKind());
        file.setContent(neo4jFile.getContent());
        file.setEntities(
            neo4jFile.getEntities() == null ? null :
                neo4jFile.getEntities().stream()
                    .map(EntityMapper::toDomain)
                    .toList()
        );
        return file;
    }

    public static Neo4jFile fromDomain(File file) {
        if (file == null) return null;
        return Neo4jFile.builder()
            .id(file.getId())
            .projectId(file.getProjectId())
            .path(file.getPath())
            .kind(file.getKind())
            .content(file.getContent())
            .entities(
                file.getEntities() == null ? null :
                    file.getEntities().stream()
                        .map(EntityMapper::fromDomain)
                        .toList()
            )
            .build();
    }
}
