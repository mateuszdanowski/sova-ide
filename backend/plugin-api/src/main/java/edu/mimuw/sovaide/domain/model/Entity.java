package edu.mimuw.sovaide.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
	private String id;
	private String projectId;
	private String name;
	private EntityKind kind;
	private String content;
//	private List<String> imports; // ?
	private List<Member> members;
	private List<Entity> implementsEntities;
}
