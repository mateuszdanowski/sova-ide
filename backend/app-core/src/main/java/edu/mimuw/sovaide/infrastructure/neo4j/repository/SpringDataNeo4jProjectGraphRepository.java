package edu.mimuw.sovaide.infrastructure.neo4j.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import edu.mimuw.sovaide.infrastructure.neo4j.entity.Neo4jProject;

@Repository
public interface SpringDataNeo4jProjectGraphRepository extends Neo4jRepository<Neo4jProject, String> {
	Neo4jProject save(Neo4jProject neo4jProject);

	List<Neo4jProject> findAll();

	Optional<Neo4jProject> findById(String id);
}
