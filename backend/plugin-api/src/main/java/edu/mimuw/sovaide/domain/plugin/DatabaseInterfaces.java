package edu.mimuw.sovaide.domain.plugin;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.model.repository.ProjectRepository;

/**
 * Provides plugins with access to database interfaces:
 * <ul>
 *   <li><b>ProjectRepository</b>: a Spring Data Neo4j facade for project-level operations</li>
 *   <li><b>GraphDBFacade</b>: a lower-level abstraction for direct access to graph nodes and edges</li>
 * </ul>
 */
public record DatabaseInterfaces(
		ProjectRepository repository,
		GraphDBFacade graphDBFacade) {
}
