package edu.mimuw.sovaide.domain.plugin;

import java.util.List;

/**
 * Interface for Sova plugins. Defines methods for plugin identification,
 * input acceptance, and execution logic.
 * <p>
 * To be recognized as a plugin by the Sova IDE, a class must implement this interface.
 */
public interface PluginSova {
    /**
     * Returns the name of the plugin. The plugin name acts as a unique identifier among plugins.
     *
     * @return the plugin name
     */
    String getName();

    /**
     * Returns the type of the plugin - either OUTPUT or INPUT
     *
     * @return the plugin type
     */
    PluginType getType();

    /**
     * Indicates whether the plugin accepts file input.
     *
     * @return true if the plugin expects a file as an input, false otherwise
     */
    boolean isAcceptingFile();

    /**
     * Returns a list of string input keys required by the plugin.
     * <p>
     * By default, returns an empty list. If the list is not empty and the plugin does not accept file input,
     * the user will be prompted with a dialog to provide the required string inputs.
     *
     * @return a list of string input keys expected by the plugin
     */
    default List<String> getStringInputs() {
        return List.of();
    }

    /**
     * Executes the plugin logic for the given project and user input.
     *
     * @param projectId the ID of the project
     * @param databaseInterfaces access to graph database facades
     * @param userInput user-provided input for the plugin
     * @return the result of plugin execution
     */
    PluginResult execute(String projectId, DatabaseInterfaces databaseInterfaces, UserInput userInput);
}
