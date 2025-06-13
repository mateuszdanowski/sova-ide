package edu.mimuw.sovaide.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;

@ConfigurationProperties(prefix = "neo4j")
@Configuration
@Setter
public class Neo4jConfig {

	private static final Logger logger = LoggerFactory.getLogger(Neo4jConfig.class);

	private String uri;
	private String username;
	private String password;

	@Bean
	public Driver neo4jDriver() {
		logger.info("Creating Neo4j driver with URI: {}", uri);
		return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
	}
}
