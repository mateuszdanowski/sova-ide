package edu.mimuw.sovaide.infrastructure.neo4j.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("Project")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jProject {
    @Id
    @GeneratedValue
    private String id;
    private String name;
    private String language;
    // to be added
    // @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    // private List<Files> files;

    // moved from legacy Project entity
    private String fileUrl;
    @Builder.Default private String status = "NOT ANALYZED";
    private Long noOfClasses;
    private Long noOfImports;
    private Long avgLinesOfCode;
}
