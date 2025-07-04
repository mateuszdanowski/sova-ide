package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.model.Entity;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jEntity;

public class EntityMapper {
    public static Entity toDomain(Neo4jEntity neo4jEntity) {
        if (neo4jEntity == null) return null;
        Entity entity = new Entity();
        entity.setId(neo4jEntity.getId());
        entity.setProjectId(neo4jEntity.getProjectId());
        entity.setName(neo4jEntity.getName());
        entity.setKind(neo4jEntity.getKind());
        entity.setContent(neo4jEntity.getContent());
        entity.setMembers(
            neo4jEntity.getMembers() == null ? null :
                neo4jEntity.getMembers().stream()
                    .map(MemberMapper::toDomain)
                    .toList()
        );return entity;
    }

    public static Neo4jEntity fromDomain(Entity entity) {
        if (entity == null) return null;
        return Neo4jEntity.builder()
            .id(entity.getId())
            .projectId(entity.getProjectId())
            .name(entity.getName())
            .kind(entity.getKind())
            .content(entity.getContent())
            .members(
                entity.getMembers() == null ? null :
                    entity.getMembers().stream()
                        .map(MemberMapper::fromDomain)
                        .toList()
            ).build();
    }
}
