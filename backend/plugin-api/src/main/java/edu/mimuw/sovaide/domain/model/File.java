package edu.mimuw.sovaide.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
	private String id;
	private String projectId;
	private String path;
	private FileKind kind;
	private String content;
	private List<Entity> entities;
}
