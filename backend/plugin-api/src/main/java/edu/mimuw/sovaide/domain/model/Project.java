package edu.mimuw.sovaide.domain.model;

import java.util.List;

import edu.mimuw.sovaide.domain.plugin.PluginResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
