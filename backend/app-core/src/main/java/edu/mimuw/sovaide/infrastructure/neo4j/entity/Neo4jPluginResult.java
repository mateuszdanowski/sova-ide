package edu.mimuw.sovaide.infrastructure.neo4j.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node("PluginResult")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jPluginResult {
	@Id
	@GeneratedValue
	private String id;
	private String projectId;
	private String pluginName;
	private String resultJson;
	// todo keep plugin type for when the plugin is removed and GUI wants to still display its result
}
