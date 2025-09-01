package edu.mimuw.sovaide.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A 'File' entity from the SOVA IDE data model.
 * Represents a file in a project, including its path, kind, content, and contained entities.
 */
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
