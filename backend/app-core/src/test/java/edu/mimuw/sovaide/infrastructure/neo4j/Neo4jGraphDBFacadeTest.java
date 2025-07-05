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

    @Test
    @DisplayName("Should update existing node with new properties")
    void testUpdateNode_Success() {
        // Given
        GraphNode createdNode = facade.createNode("Person", Map.of("name", "John", "age", 30));
        Map<String, Object> updates = Map.of("age", 31, "city", "Warsaw");

        // When
        Optional<GraphNode> updatedNode = facade.updateNode(createdNode.getId(), updates);

        // Then
        assertTrue(updatedNode.isPresent());
        assertEquals(createdNode.getId(), updatedNode.get().getId());
        assertEquals("Person", updatedNode.get().getLabel());
        assertEquals("John", updatedNode.get().getProperties().get("name")); // Original property preserved
        assertEquals(31L, updatedNode.get().getProperties().get("age")); // Updated property
        assertEquals("Warsaw", updatedNode.get().getProperties().get("city")); // New property
    }

    @Test
    @DisplayName("Should update node with empty properties map")
    void testUpdateNode_EmptyProperties() {
        // Given
        GraphNode createdNode = facade.createNode("Person", Map.of("name", "John"));
        Map<String, Object> emptyUpdates = Collections.emptyMap();

        // When
        Optional<GraphNode> updatedNode = facade.updateNode(createdNode.getId(), emptyUpdates);

        // Then
        assertTrue(updatedNode.isPresent());
        assertEquals(createdNode.getId(), updatedNode.get().getId());
        assertEquals("John", updatedNode.get().getProperties().get("name")); // Original property preserved
    }

    @Test
    @DisplayName("Should return empty when updating non-existent node")
    void testUpdateNode_NonExistentNode() {
        // Given
        String nonExistentId = "999999";
        Map<String, Object> updates = Map.of("name", "Updated Name");

        // When
        Optional<GraphNode> result = facade.updateNode(nonExistentId, updates);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should update node and preserve existing relationships")
    void testUpdateNode_PreservesRelationships() {
        // Given
        GraphNode person = facade.createNode("Person", Map.of("name", "John"));
        GraphNode company = facade.createNode("Company", Map.of("name", "TechCorp"));
        facade.createEdge(person, company, "WORKS_FOR", Map.of("role", "Developer"));

        // When
        Optional<GraphNode> updatedPerson = facade.updateNode(person.getId(), Map.of("age", 30));

        // Then
        assertTrue(updatedPerson.isPresent());
        assertEquals(30L, updatedPerson.get().getProperties().get("age"));

        // Verify relationship still exists
        List<GraphEdge> edges = facade.getEdges(updatedPerson.get(), "WORKS_FOR", EdgeDirection.OUTGOING);
        assertEquals(1, edges.size());
        assertEquals("Developer", edges.getFirst().getProperties().get("role"));
    }

    @Test
    @DisplayName("Should handle special data types in update")
    void testUpdateNode_SpecialDataTypes() {
        // Given
        GraphNode node = facade.createNode("TestNode", Map.of("initial", "value"));
        Map<String, Object> updates = Map.of(
            "text", "Updated text",
            "number", 42,
            "decimal", 3.14,
            "boolean", true,
            "list", List.of("item1", "item2")
        );

        // When
        Optional<GraphNode> updatedNode = facade.updateNode(node.getId(), updates);

        // Then
        assertTrue(updatedNode.isPresent());
        Map<String, Object> properties = updatedNode.get().getProperties();
        assertEquals("Updated text", properties.get("text"));
        assertEquals(42L, properties.get("number")); // Neo4j returns integers as Long
        assertEquals(3.14, properties.get("decimal"));
        assertEquals(true, properties.get("boolean"));
        assertEquals(List.of("item1", "item2"), properties.get("list"));
        assertEquals("value", properties.get("initial")); // Original property preserved
    }

    @Test
    @DisplayName("Should overwrite existing property values")
    void testUpdateNode_OverwriteExisting() {
        // Given
        GraphNode node = facade.createNode("Person", Map.of("name", "John", "age", 30, "city", "Krakow"));
        Map<String, Object> updates = Map.of("name", "Johnny", "age", 31);

        // When
        Optional<GraphNode> updatedNode = facade.updateNode(node.getId(), updates);

        // Then
        assertTrue(updatedNode.isPresent());
        Map<String, Object> properties = updatedNode.get().getProperties();
        assertEquals("Johnny", properties.get("name")); // Overwritten
        assertEquals(31L, properties.get("age")); // Overwritten
        assertEquals("Krakow", properties.get("city")); // Preserved
    }

    @Test
    @DisplayName("Should delete all nodes with specified property value")
    void testDeleteAllWithProperty_Success() {
        // Given
        facade.createNode("Person", Map.of("name", "John", "projectId", "project-1"));
        facade.createNode("Person", Map.of("name", "Jane", "projectId", "project-1"));
        facade.createNode("Person", Map.of("name", "Bob", "projectId", "project-2"));
        facade.createNode("Company", Map.of("name", "TechCorp", "projectId", "project-1"));

        // When
        facade.deleteAllWithProperty("projectId", "project-1");

        // Then
        List<GraphNode> remainingNodes = facade.findNodes("Person", Collections.emptyMap());
        assertEquals(1, remainingNodes.size());
        assertEquals("Bob", remainingNodes.getFirst().getProperties().get("name"));

        List<GraphNode> remainingCompanies = facade.findNodes("Company", Collections.emptyMap());
        assertEquals(0, remainingCompanies.size());
    }

    @Test
    @DisplayName("Should delete nodes and their relationships")
    void testDeleteAllWithProperty_WithRelationships() {
        // Given
        GraphNode person1 = facade.createNode("Person", Map.of("name", "John", "projectId", "project-1"));
        GraphNode person2 = facade.createNode("Person", Map.of("name", "Jane", "projectId", "project-2"));
        GraphNode company = facade.createNode("Company", Map.of("name", "TechCorp", "projectId", "project-1"));

        facade.createEdge(person1, company, "WORKS_FOR", Collections.emptyMap());
        facade.createEdge(person2, person1, "KNOWS", Collections.emptyMap());

        // When
        facade.deleteAllWithProperty("projectId", "project-1");

        // Then
        List<GraphNode> remainingPersons = facade.findNodes("Person", Collections.emptyMap());
        assertEquals(1, remainingPersons.size());
        assertEquals("Jane", remainingPersons.getFirst().getProperties().get("name"));

        // Verify relationships are also deleted
        List<GraphEdge> edges = facade.getEdges(remainingPersons.getFirst(), null, EdgeDirection.OUTGOING);
        assertEquals(0, edges.size()); // The KNOWS relationship should be deleted since person1 was deleted
    }

    @Test
    @DisplayName("Should handle deletion when no nodes match the property")
    void testDeleteAllWithProperty_NoMatches() {
        // Given
        facade.createNode("Person", Map.of("name", "John", "projectId", "project-1"));

        // When - delete with non-existent property value
        facade.deleteAllWithProperty("projectId", "non-existent");

        // Then - original node should still exist
        List<GraphNode> nodes = facade.findNodes("Person", Collections.emptyMap());
        assertEquals(1, nodes.size());
        assertEquals("John", nodes.getFirst().getProperties().get("name"));
    }

    @Test
    @DisplayName("Should handle deletion with different data types")
    void testDeleteAllWithProperty_DifferentDataTypes() {
        // Given
        facade.createNode("Task", Map.of("id", 123, "status", "active"));
        facade.createNode("Task", Map.of("id", 456, "status", "active"));
        facade.createNode("Task", Map.of("id", 789, "status", "completed"));

        // When - delete by integer property
        facade.deleteAllWithProperty("id", 123);

        // Then
        List<GraphNode> remainingTasks = facade.findNodes("Task", Collections.emptyMap());
        assertEquals(2, remainingTasks.size());
        assertTrue(remainingTasks.stream().noneMatch(task ->
            Integer.valueOf(123).equals(task.getProperties().get("id"))));
    }

    @Test
    @DisplayName("Should handle deletion with boolean properties")
    void testDeleteAllWithProperty_BooleanProperty() {
        // Given
        facade.createNode("User", Map.of("name", "John", "active", true));
        facade.createNode("User", Map.of("name", "Jane", "active", true));
        facade.createNode("User", Map.of("name", "Bob", "active", false));

        // When - delete inactive users
        facade.deleteAllWithProperty("active", false);

        // Then
        List<GraphNode> remainingUsers = facade.findNodes("User", Collections.emptyMap());
        assertEquals(2, remainingUsers.size());
        assertTrue(remainingUsers.stream().allMatch(user ->
            Boolean.TRUE.equals(user.getProperties().get("active"))));
    }

}
