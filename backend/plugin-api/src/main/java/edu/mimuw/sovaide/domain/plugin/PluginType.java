package edu.mimuw.sovaide.domain.plugin;

/**
 * Defines the type of plugin in SOVA IDE.
 * <p>
 * <b>Input plugins</b> import and process external data sources, such as JAR files, log files, or git repositories.
 * Their main role is to parse and analyze this data, then store the results in the database.
 * Input plugins act as importers into SOVA IDE's data model.
 * <p>
 * <b>Output plugins</b> operate on data already present in the database, often produced by other plugins.
 * They can generate new derived data or present results to the user.
 */
public enum PluginType {
	INPUT,
	OUTPUT
}
