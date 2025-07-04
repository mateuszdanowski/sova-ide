package edu.mimuw.sovaide.infrastructure.neo4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import edu.mimuw.sovaide.domain.graph.EdgeDirection;
import edu.mimuw.sovaide.domain.graph.GraphEdge;
import edu.mimuw.sovaide.domain.graph.GraphNode;

@Testcontainers
class Neo4jGraphDBFacadeTest {

    @Container
    static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.26.8")
            .withAdminPassword("password");

    private static Neo4jGraphDBFacade facade;

    @BeforeAll
    static void setupClass() {
        facade = new Neo4jGraphDBFacade(
                neo4jContainer.getBoltUrl(),
                "neo4j",
                "password"
        );
    }

    @AfterAll
    static void tearDownClass() {
        if (facade != null) {
            facade.close();
        }
    }

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        facade.executeCypher("MATCH (n) DETACH DELETE n", Collections.emptyMap());
    }

    @Test
    @DisplayName("Should create a node with label and properties")
    void testCreateNode() {
        // Given
        String label = "Person";
        Map<String, Object> properties = Map.of(
                "name", "John Doe",
                "age", 30,
                "active", true
        );

        // When
        GraphNode node = facade.createNode(label, properties);

        // Then
        assertNotNull(node);
        assertNotNull(node.getId());
        assertEquals(label, node.getLabel());
        assertEquals("John Doe", node.getProperties().get("name"));
        assertEquals(30L, node.getProperties().get("age")); // Neo4j returns integers as Long
        assertEquals(true, node.getProperties().get("active"));
    }

    @Test
    @DisplayName("Should create a node with empty properties")
    void testCreateNodeWithEmptyProperties() {
        // Given
        String label = "EmptyNode";
        Map<String, Object> properties = Collections.emptyMap();

        // When
        GraphNode node = facade.createNode(label, properties);

        // Then
        assertNotNull(node);
        assertNotNull(node.getId());
        assertEquals(label, node.getLabel());
        assertTrue(node.getProperties().isEmpty());
    }

    @Test
    @DisplayName("Should get node by ID when node exists")
    void testGetNodeById_ExistingNode() {
        // Given
        GraphNode createdNode = facade.createNode("TestNode", Map.of("key", "value"));

        // When
        Optional<GraphNode> foundNode = facade.getNodeById(createdNode.getId());

        // Then
        assertTrue(foundNode.isPresent());
        assertEquals(createdNode.getId(), foundNode.get().getId());
        assertEquals(createdNode.getLabel(), foundNode.get().getLabel());
        assertEquals("value", foundNode.get().getProperties().get("key"));
    }

    @Test
    @DisplayName("Should return empty when getting node by non-existent ID")
    void testGetNodeById_NonExistentNode() {
        // When
        Optional<GraphNode> foundNode = facade.getNodeById("999999");

        // Then
        assertFalse(foundNode.isPresent());
    }

    @Test
    @DisplayName("Should find nodes by label without filters")
    void testFindNodes_ByLabelOnly() {
        // Given
        facade.createNode("Product", Map.of("name", "Laptop"));
        facade.createNode("Product", Map.of("name", "Mouse"));
        facade.createNode("Person", Map.of("name", "John"));

        // When
        List<GraphNode> products = facade.findNodes("Product", Collections.emptyMap());

        // Then
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(node -> "Product".equals(node.getLabel())));
    }

    @Test
    @DisplayName("Should find nodes by label with filters")
    void testFindNodes_WithFilters() {
        // Given
        facade.createNode("Product", Map.of("name", "Laptop", "price", 1000));
        facade.createNode("Product", Map.of("name", "Mouse", "price", 50));
        facade.createNode("Product", Map.of("name", "Keyboard", "price", 100));

        // When
        List<GraphNode> expensiveProducts = facade.findNodes("Product", Map.of("price", 1000));

        // Then
        assertEquals(1, expensiveProducts.size());
        assertEquals("Laptop", expensiveProducts.getFirst().getProperties().get("name"));
    }

    @Test
    @DisplayName("Should find nodes with multiple filters")
    void testFindNodes_MultipleFilters() {
        // Given
        facade.createNode("Person", Map.of("name", "John", "age", 30, "city", "Warsaw"));
        facade.createNode("Person", Map.of("name", "Jane", "age", 25, "city", "Warsaw"));
        facade.createNode("Person", Map.of("name", "Bob", "age", 30, "city", "Krakow"));

        // When
        List<GraphNode> filtered = facade.findNodes("Person", Map.of("age", 30, "city", "Warsaw"));

        // Then
        assertEquals(1, filtered.size());
        assertEquals("John", filtered.getFirst().getProperties().get("name"));
    }

    @Test
    @DisplayName("Should create edge between two nodes")
    void testCreateEdge() {
        // Given
        GraphNode person = facade.createNode("Person", Map.of("name", "John"));
        GraphNode company = facade.createNode("Company", Map.of("name", "TechCorp"));
        Map<String, Object> edgeProperties = Map.of("role", "Developer", "since", 2020);

        // When
        GraphEdge edge = facade.createEdge(person, company, "WORKS_FOR", edgeProperties);

        // Then
        assertNotNull(edge);
        assertNotNull(edge.getId());
        assertEquals("WORKS_FOR", edge.getType());
        assertEquals("Developer", edge.getProperties().get("role"));
        assertEquals(2020L, edge.getProperties().get("since"));
        assertEquals(person.getId(), edge.getStartNode().getId());
        assertEquals(company.getId(), edge.getEndNode().getId());
    }

    @Test
    @DisplayName("Should create edge with empty properties")
    void testCreateEdge_EmptyProperties() {
        // Given
        GraphNode nodeA = facade.createNode("NodeA", Collections.emptyMap());
        GraphNode nodeB = facade.createNode("NodeB", Collections.emptyMap());

        // When
        GraphEdge edge = facade.createEdge(nodeA, nodeB, "CONNECTED_TO", Collections.emptyMap());

        // Then
        assertNotNull(edge);
        assertEquals("CONNECTED_TO", edge.getType());
        assertTrue(edge.getProperties().isEmpty());
    }

    @Test
    @DisplayName("Should get outgoing edges")
    void testGetEdges_Outgoing() {
        // Given
        GraphNode person = facade.createNode("Person", Map.of("name", "John"));
        GraphNode company1 = facade.createNode("Company", Map.of("name", "Company1"));
        GraphNode company2 = facade.createNode("Company", Map.of("name", "Company2"));

        facade.createEdge(person, company1, "WORKS_FOR", Collections.emptyMap());
        facade.createEdge(person, company2, "WORKS_FOR", Collections.emptyMap());
        facade.createEdge(company1, person, "EMPLOYS", Collections.emptyMap()); // Incoming edge

        // When
        List<GraphEdge> outgoingEdges = facade.getEdges(person, "WORKS_FOR", EdgeDirection.OUTGOING);

        // Then
        assertEquals(2, outgoingEdges.size());
        assertTrue(outgoingEdges.stream().allMatch(edge ->
            "WORKS_FOR".equals(edge.getType()) && person.getId().equals(edge.getStartNode().getId())
        ));
    }

    @Test
    @DisplayName("Should get incoming edges")
    void testGetEdges_Incoming() {
        // Given
        GraphNode person = facade.createNode("Person", Map.of("name", "John"));
        GraphNode company1 = facade.createNode("Company", Map.of("name", "Company1"));
        GraphNode company2 = facade.createNode("Company", Map.of("name", "Company2"));

		facade.createEdge(company1, person, "EMPLOYS", Collections.emptyMap());
		facade.createEdge(company2, person, "EMPLOYS", Collections.emptyMap());
		facade.createEdge(person, company1, "WORKS_FOR", Collections.emptyMap());

        // When
        List<GraphEdge> incomingEdges = facade.getEdges(person, "EMPLOYS", EdgeDirection.INCOMING);

        // Then
        assertEquals(2, incomingEdges.size());
        assertTrue(incomingEdges.stream().allMatch(edge ->
            "EMPLOYS".equals(edge.getType()) && person.getId().equals(edge.getEndNode().getId())
        ));
    }

    @Test
    @DisplayName("Should get edges of any type when type is null")
    void testGetEdges_AnyType() {
        // Given
        GraphNode person = facade.createNode("Person", Map.of("name", "John"));
        GraphNode company = facade.createNode("Company", Map.of("name", "TechCorp"));
        GraphNode friend = facade.createNode("Person", Map.of("name", "Jane"));

        facade.createEdge(person, company, "WORKS_FOR", Collections.emptyMap());
        facade.createEdge(person, friend, "KNOWS", Collections.emptyMap());

        // When
        List<GraphEdge> allEdges = facade.getEdges(person, null, EdgeDirection.OUTGOING);

        // Then
        assertEquals(2, allEdges.size());
        Set<String> edgeTypes = Set.of(allEdges.getFirst().getType(), allEdges.get(1).getType());
        assertTrue(edgeTypes.contains("WORKS_FOR"));
        assertTrue(edgeTypes.contains("KNOWS"));
    }

    @Test
    @DisplayName("Should execute simple Cypher query")
    void testExecuteCypher_SimpleQuery() {
        // Given
        facade.createNode("Person", Map.of("name", "John", "age", 30));
        facade.createNode("Person", Map.of("name", "Jane", "age", 25));

        // When
        List<Map<String, Object>> results = facade.executeCypher(
            "MATCH (p:Person) RETURN p.name AS name, p.age AS age ORDER BY p.age",
            Collections.emptyMap()
        );

        // Then
        assertEquals(2, results.size());
        assertEquals("Jane", results.getFirst().get("name"));
        assertEquals(25L, results.getFirst().get("age"));
        assertEquals("John", results.get(1).get("name"));
        assertEquals(30L, results.get(1).get("age"));
    }

    @Test
    @DisplayName("Should execute Cypher query with parameters")
    void testExecuteCypher_WithParameters() {
        // Given
        facade.createNode("Person", Map.of("name", "John", "age", 30));
        facade.createNode("Person", Map.of("name", "Jane", "age", 25));
        facade.createNode("Person", Map.of("name", "Bob", "age", 35));

        // When
        List<Map<String, Object>> results = facade.executeCypher(
            "MATCH (p:Person) WHERE p.age >= $minAge RETURN p.name AS name ORDER BY p.age",
            Map.of("minAge", 30)
        );

        // Then
        assertEquals(2, results.size());
        assertEquals("John", results.getFirst().get("name"));
        assertEquals("Bob", results.get(1).get("name"));
    }

    @Test
    @DisplayName("Should execute Cypher query returning nodes")
    void testExecuteCypher_ReturningNodes() {
        // Given
        facade.createNode("Person", Map.of("name", "John"));

        // When
        List<Map<String, Object>> results = facade.executeCypher(
            "MATCH (p:Person) RETURN p",
            Collections.emptyMap()
        );

        // Then
        assertEquals(1, results.size());
        assertNotNull(results.getFirst().get("p"));
    }

    @Test
    @DisplayName("Should handle empty results from Cypher query")
    void testExecuteCypher_EmptyResults() {
        // When
        List<Map<String, Object>> results = facade.executeCypher(
            "MATCH (p:NonExistentLabel) RETURN p",
            Collections.emptyMap()
        );

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle concurrent operations")
    void testConcurrentOperations() {
        // This test ensures thread safety of the facade
        List<Thread> threads = new ArrayList<>();
        List<GraphNode> createdNodes = Collections.synchronizedList(new ArrayList<>());

        // Create multiple threads that create nodes concurrently
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                GraphNode node = facade.createNode("ConcurrentTest", Map.of("threadId", threadId));
                createdNodes.add(node);
            });
            threads.add(thread);
        }

        // Start all threads
        threads.forEach(Thread::start);

        // Wait for all threads to complete
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread was interrupted");
            }
        });

        // Verify all nodes were created
        assertEquals(5, createdNodes.size());
        List<GraphNode> foundNodes = facade.findNodes("ConcurrentTest", Collections.emptyMap());
        assertEquals(5, foundNodes.size());
    }
}
