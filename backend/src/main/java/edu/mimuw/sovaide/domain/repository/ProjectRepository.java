package edu.mimuw.sovaide.domain.repository;

import java.util.List;
import java.util.Optional;

import edu.mimuw.sovaide.domain.Project;

public interface ProjectRepository {
	Optional<Project> findById(String id);

	List<Project> findAll();

	Project save(Project project);

	void deleteById(String id);
}
