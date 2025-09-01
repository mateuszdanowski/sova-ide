package edu.mimuw.sovaide.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An 'Entity' entity from the SOVA IDE data model.
 * Represents a code entity within a project, such as a class, interface, record, or enum.
 * Stores its name, type (see {@link EntityKind}), content, and associated members.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
	private String id;
	private String projectId;
	private String name;
	private EntityKind kind;
	private String content;
	private List<Member> members;
}
