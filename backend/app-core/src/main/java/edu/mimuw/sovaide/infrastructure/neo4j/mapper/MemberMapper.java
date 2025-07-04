package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.model.Member;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jMember;

public class MemberMapper {
    public static Member toDomain(Neo4jMember neo4jMember) {
        if (neo4jMember == null) return null;
        Member member = new Member();
        member.setId(neo4jMember.getId());
        member.setProjectId(neo4jMember.getProjectId());
        member.setName(neo4jMember.getName());
        member.setKind(neo4jMember.getKind());
        member.setContent(neo4jMember.getContent());
        return member;
    }

    public static Neo4jMember fromDomain(Member member) {
        if (member == null) return null;
        return Neo4jMember.builder()
            .id(member.getId())
            .projectId(member.getProjectId())
            .name(member.getName())
            .kind(member.getKind())
            .content(member.getContent())
            .build();
    }
}
