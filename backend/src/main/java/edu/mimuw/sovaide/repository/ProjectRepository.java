package edu.mimuw.sovaide.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import edu.mimuw.sovaide.domain.Project;

@Repository
public interface ProjectRepository extends Neo4jRepository<Project, String> {
	Optional<Project> findById(String id);
}
