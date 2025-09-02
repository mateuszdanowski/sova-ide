package edu.mimuw.sovaide.domain.plugin.frontend;

/**
 * Defines the set of frontend component types currently supported in SOVA IDE.
 * <p>
 * Each value represents a way to present plugin results in the frontend. The frontend maintains a corresponding list of components
 * in <b>pluginResultComponentRegistry</b>; whenever this enum is updated, the registry should be updated as well to ensure consistency.
 * <p>
 * The component type determines how the data in {@link GuiComponentData} is rendered for the user.
 */
public enum FrontendComponentType {
    /**
     * Renders a plain graph visualization.
     */
    Graph,
    /**
     * Renders provided HTML content.
     */
    HTML,
    /**
     * Simply renders the provided text.
     */
    Text,
    /**
     * Creates a horizontal bar chart from the provided data.
     */
    BarChart
}
