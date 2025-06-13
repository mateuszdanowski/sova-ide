package edu.mimuw.sovaide.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.mimuw.sovaide.domain.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
	Optional<Project> findById(String id);
}
