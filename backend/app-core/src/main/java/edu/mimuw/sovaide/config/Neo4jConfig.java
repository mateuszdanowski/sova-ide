package edu.mimuw.sovaide.config;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.infrastructure.neo4j.Neo4jGraphDBFacade;

@Configuration
public class Neo4jConfig {

	@Bean
	org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
		return org.neo4j.cypherdsl.core.renderer.Configuration.newConfig()
				.withDialect(Dialect.NEO4J_5).build();
	}

	@Bean
	public GraphDBFacade graphDBFacade() {
		return new Neo4jGraphDBFacade("bolt://localhost:7687", "neo4j", "password");
	}
}
