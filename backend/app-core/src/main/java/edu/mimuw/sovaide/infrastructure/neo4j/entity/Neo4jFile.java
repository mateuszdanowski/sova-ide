package edu.mimuw.sovaide.infrastructure.neo4j.entity;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import edu.mimuw.sovaide.domain.model.FileKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("File")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jFile {
    @Id
    @GeneratedValue
    private String id;
    private String path;
    private FileKind kind;
    private String content;
    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jEntity> entities;
}
