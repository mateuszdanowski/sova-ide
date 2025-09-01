package edu.mimuw.sovaide.domain.graph;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Facade interface for graph database operations, providing methods for node and edge management.
 * <p>
 * Also supports custom Cypher queries as a fallback, but use with caution, as this may hinder future database engine replacement.
 */
public interface GraphDBFacade {
    /**
     * Creates a new node in the graph database with the specified label and properties.
     *
     * @param label the label for the node
     * @param properties a map of properties to assign to the node
     * @return the created GraphNode
     */
    GraphNode createNode(String label, Map<String, Object> properties);

    /**
     * Updates the properties of an existing node identified by its ID.
     *
     * @param id the unique identifier of the node
     * @param properties a map of properties to update
     * @return an Optional containing the updated GraphNode, or empty if not found
     */
    Optional<GraphNode> updateNode(String id, Map<String, Object> properties);

    /**
     * Retrieves a node from the graph database by its unique ID.
     *
     * @param id the unique identifier of the node
     * @return an Optional containing the found GraphNode, or empty if not found
     */
    Optional<GraphNode> getNodeById(String id);

    /**
     * Finds nodes in the graph database by label and optional property filters.
     *
     * @param label the label to filter nodes
     * @param filters a map of property filters to apply
     * @return a list of matching GraphNode objects
     */
    List<GraphNode> findNodes(String label, Map<String, Object> filters);

    /**
     * Deletes all nodes that have a specific property value.
     *
     * @param propertyName the name of the property
     * @param propertyValue the value of the property to match for deletion
     */
    void deleteAllWithProperty(String propertyName, Object propertyValue);

    /**
     * Creates a new edge between two nodes in the graph database.
     *
     * @param from the start node
     * @param to the end node
     * @param type the type (label) of the edge
     * @param properties a map of properties to assign to the edge
     * @return the created GraphEdge
     */
    GraphEdge createEdge(GraphNode from, GraphNode to, String type, Map<String, Object> properties);

    /**
     * Retrieves edges connected to a node, filtered by type and direction.
     *
     * @param node the node for which to retrieve edges
     * @param type the type (label) of edges to retrieve
     * @param direction the direction of the edges (incoming or outgoing)
     * @return a list of matching GraphEdge objects
     */
    List<GraphEdge> getEdges(GraphNode node, String type, EdgeDirection direction);

    /**
     * Executes a custom Cypher query against the graph database.
     * <p>
     * Use with caution, as reliance on Cypher may hinder future database engine replacement.
     *
     * @param cypher the Cypher query string
     * @param parameters a map of parameters for the query
     * @return a list of result maps, each representing a row returned by the query
     */
    List<Map<String, Object>> executeCypher(String cypher, Map<String, Object> parameters);
}
