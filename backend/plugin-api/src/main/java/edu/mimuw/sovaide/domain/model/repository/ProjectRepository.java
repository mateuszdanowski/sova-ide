package edu.mimuw.sovaide.domain.model.repository;

import java.util.List;
import java.util.Optional;

import edu.mimuw.sovaide.domain.model.Project;

/**
 * Repository interface for managing SOVA IDE's data model in the database.
 * For advanced or flexible graph operations, use {@link edu.mimuw.sovaide.domain.graph.GraphDBFacade} instead.
 */
public interface ProjectRepository {
    /**
     * Finds a project by its unique identifier.
     *
     * @param id the project ID
     * @return an Optional containing the found Project, or empty if not found
     */
    Optional<Project> findById(String id);

    /**
     * Retrieves all projects from the database.
     *
     * @return a list of all Project entities
     */
    List<Project> findAll();

    /**
     * Saves a project to the database. If the project already exists, it will be updated.
     *
     * @param project the Project entity to save
     * @return the saved Project entity
     */
    Project save(Project project);

    /**
     * Deletes a project by its unique identifier.
     *
     * @param id the project ID
     */
    void deleteById(String id);
}
