package edu.mimuw.sovaide.infrastructure.neo4j.entity;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import edu.mimuw.sovaide.domain.model.EntityKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("Entity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jEntity {
    @Id
    @GeneratedValue
    private String id;
    private String projectId;
    private String name;
    private EntityKind kind;
    private String content;
    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jMember> members;
}
