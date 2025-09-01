package edu.mimuw.sovaide.domain.plugin;

import java.util.Map;

/**
 * Represents user input for a plugin. Input can be:
 * <ul>
 *   <li><b>fileUrl</b>: Provided when the plugin accepts files via {@link PluginSova#isAcceptingFile()}.</li>
 *   <li><b>properties</b>: A map of string values defined by the plugin via {@link PluginSova#getStringInputs()}.</li>
 * </ul>
 */
public record UserInput(String fileUrl, Map<String, String> properties) {
}
