package edu.mimuw.sovaide.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A 'Member' entity from the SOVA IDE data model.
 * Represents a member of a code entity, such as a field, method, or constructor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private String id;
	private String projectId;
	private String name;
	private MemberKind kind;
	private String content;
}
