package edu.mimuw.sovaide;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Neo4jConfigTest {

	@Autowired
	private Driver neo4jDriver;

	@Test
	void contextLoads() {
		neo4jDriver.verifyConnectivity();
	}
}
