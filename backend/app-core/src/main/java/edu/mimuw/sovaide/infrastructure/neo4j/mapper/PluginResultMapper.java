package edu.mimuw.sovaide.infrastructure.neo4j.mapper;

import edu.mimuw.sovaide.domain.plugin.GuiComponentData;
import edu.mimuw.sovaide.domain.plugin.PluginResult;
import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jPluginResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PluginResultMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static PluginResult toDomain(Neo4jPluginResult neo4jPluginResult) {
		if (neo4jPluginResult == null) return null;

		// deserialize into an object containing three fields: componentType, data and config
		GuiComponentData deserializedGuiComponentData;
		try {
			deserializedGuiComponentData = objectMapper.readValue(
				neo4jPluginResult.getResultJson(),
				GuiComponentData.class
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to deserialize GuiComponentData from JSON", e);
		}

		return PluginResult.builder()
			.projectId(neo4jPluginResult.getProjectId())
			.pluginName(neo4jPluginResult.getPluginName())
			.guiComponentData(deserializedGuiComponentData)
			.build();
	}

	public static Neo4jPluginResult fromDomain(PluginResult pluginResult) {
		if (pluginResult == null) return null;

		// serialize GuiComponentData to JSON string
		String serializedGuiComponentData;
		try {
			serializedGuiComponentData = objectMapper.writeValueAsString(pluginResult.guiComponentData());
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize GuiComponentData to JSON", e);
		}

		return Neo4jPluginResult.builder()
			.projectId(pluginResult.projectId())
			.pluginName(pluginResult.pluginName())
			.resultJson(serializedGuiComponentData)
			.build();
	}
}
