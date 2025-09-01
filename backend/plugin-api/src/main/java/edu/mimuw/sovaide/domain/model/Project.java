package edu.mimuw.sovaide.domain.model;

import java.util.List;

import edu.mimuw.sovaide.domain.plugin.PluginResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A 'Project' entity from the SOVA IDE data model.
 * Represents a software project, including its files and plugin results executed on it.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
	private String id;
	private String name;
	private List<File> files;
	private List<PluginResult> results;
}
