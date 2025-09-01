package edu.mimuw.sovaide.domain.plugin;

import java.util.Map;

/**
 * Contains data and configuration for a GUI component that a plugin wants to use to display results.
 * <p>
 * The {@code componentType} determines how the data is presented in the frontend. Refer to each value in
 * {@link FrontendComponentType} for details on the expected data and configuration for each component type.
 */
public record GuiComponentData(FrontendComponentType componentType,
							   Map<String, Object> data,
							   Map<String, Object> config) {
}
