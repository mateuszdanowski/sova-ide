package edu.mimuw.sovaide.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
