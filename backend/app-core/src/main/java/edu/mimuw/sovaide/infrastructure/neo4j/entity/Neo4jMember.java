package edu.mimuw.sovaide.infrastructure.neo4j.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import edu.mimuw.sovaide.domain.model.MemberKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("Member")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jMember {
    @Id
    @GeneratedValue
    private String id;
    private String name;
    private MemberKind kind;
    private String content;
//    @Relationship(type = "CALLS", direction = Relationship.Direction.OUTGOING)
//    private List<Neo4jMember> calls;
//    @Relationship(type = "IS_OF_TYPE", direction = Relationship.Direction.OUTGOING)
//    private Neo4jEntity isOfType;
}
