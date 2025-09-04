package edu.mimuw.sovaide.infrastructure.neo4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.mimuw.sovaide.domain.graph.EdgeDirection;
import edu.mimuw.sovaide.domain.graph.GraphDBFacade;
import edu.mimuw.sovaide.domain.graph.GraphEdge;
import edu.mimuw.sovaide.domain.graph.GraphNode;
import edu.mimuw.sovaide.infrastructure.neo4j.mapper.GraphEdgeMapper;
import edu.mimuw.sovaide.infrastructure.neo4j.mapper.GraphNodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class Neo4jGraphDBFacade implements GraphDBFacade, AutoCloseable {

	@Autowired
	private final Driver driver;

	/* to be used in tests
		public Neo4jGraphDBFacade(String uri, String user, String password) {
			this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		}
	 */

    @Override
    public GraphNode createNode(String label, Map<String, Object> properties) {
        try (Session session = driver.session()) {
            StringBuilder cypher = new StringBuilder("CREATE (n:" + label + ") ");
            if (!properties.isEmpty()) {
                cypher.append("SET n += $props ");
            }
            cypher.append("RETURN n");
            Record record = session.executeWrite(tx ->
					tx.run(cypher.toString(), Collections.singletonMap("props", properties)).single());
            return GraphNodeMapper.from(record.get("n").asNode());
        }
    }

	@Override
	public Optional<GraphNode> updateNode(String id, Map<String, Object> properties) {
		try (Session session = driver.session()) {
			String cypher = "MATCH (n) WHERE elementId(n) = $id SET n += $props RETURN n";
			Map<String, Object> params = new HashMap<>();
			params.put("id", id);
			params.put("props", properties);

			Record record = session.executeWrite(tx -> {
				Result result = tx.run(cypher, params);
				return result.hasNext() ? result.next() : null;
			});

			if (record != null) {
				return Optional.of(GraphNodeMapper.from(record.get("n").asNode()));
			}
			return Optional.empty();
		} catch (Exception e) {
			log.error("Error updating node with id {}: {}", id, e.getMessage());
			return Optional.empty();
		}
	}

    @Override
    public Optional<GraphNode> getNodeById(String id) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (n) WHERE elementId(n) = $id RETURN n";
            Result result = session.run(cypher, Collections.singletonMap("id", id));
            if (result.hasNext()) {
                Record record = result.next();
                return Optional.of(GraphNodeMapper.from(record.get("n").asNode()));
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<GraphNode> findNodes(String label, Map<String, Object> filters) {
        try (Session session = driver.session()) {
            StringBuilder cypher = new StringBuilder("MATCH (n:" + label + ")");
            if (!filters.isEmpty()) {
                cypher.append(" WHERE ");
                List<String> conds = new ArrayList<>();
                for (String key : filters.keySet()) {
                    conds.add("n." + key + " = $" + key);
                }
                cypher.append(String.join(" AND ", conds));
            }
            cypher.append(" RETURN n");
            Result result = session.run(cypher.toString(), filters);
            List<GraphNode> nodes = new ArrayList<>();
            while (result.hasNext()) {
                nodes.add(GraphNodeMapper.from(result.next().get("n").asNode()));
            }
            return nodes;
        }
    }

    @Override
    public GraphEdge createEdge(GraphNode from, GraphNode to, String type, Map<String, Object> properties) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (a), (b) WHERE elementId(a) = $fromId AND elementId(b) = $toId CREATE (a)-[r:" + type + "]->(b) SET r += $props RETURN r, a, b";
            Map<String, Object> params = new HashMap<>();
            params.put("fromId", from.getId());
            params.put("toId", to.getId());
            params.put("props", properties);
            Record record = session.executeWrite(tx -> tx.run(cypher, params).single());
            return GraphEdgeMapper.from(record.get("r").asRelationship(), from, to);
        }
    }

    @Override
    public List<GraphEdge> getEdges(GraphNode node, String type, EdgeDirection direction) {
        try (Session session = driver.session()) {
            String rel = type != null ? ":" + type : "";
            String pattern = switch (direction) {
                case OUTGOING -> "(n)-[r" + rel + "]->(m)";
                case INCOMING -> "(m)-[r" + rel + "]->(n)";
            };
            String cypher = "MATCH " + pattern + " WHERE elementId(n) = $id RETURN r, n, m";
            Map<String, Object> params = new HashMap<>();
            params.put("id", node.getId());
            Result result = session.run(cypher, params);
            List<GraphEdge> edges = new ArrayList<>();
            while (result.hasNext()) {
                Record rec = result.next();
                GraphNode nodeN = GraphNodeMapper.from(rec.get("n").asNode());
                GraphNode nodeM = GraphNodeMapper.from(rec.get("m").asNode());

                // Determine the correct from/to nodes based on direction
                GraphNode from, to;
                if (direction == EdgeDirection.OUTGOING) {
                    // Pattern: (n)-[r]->(m), so n is from, m is to
                    from = nodeN;
                    to = nodeM;
                } else { // INCOMING
                    // Pattern: (m)-[r]->(n), so m is from, n is to
                    from = nodeM;
                    to = nodeN;
                }

                edges.add(GraphEdgeMapper.from(rec.get("r").asRelationship(), from, to));
            }
            return edges;
        }
    }

    @Override
    public List<Map<String, Object>> executeCypher(String cypher, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            Result result = session.run(cypher, parameters);
            List<Map<String, Object>> list = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> map = new HashMap<>();
                for (String key : record.keys()) {
                    map.put(key, record.get(key).asObject());
                }
                list.add(map);
            }
            return list;
        }
    }

    @Override
    public void deleteAllWithProperty(String propertyName, Object propertyValue) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (n) WHERE n." + propertyName + " = $value DETACH DELETE n";
            Map<String, Object> params = Map.of("value", propertyValue);

            session.executeWrite(tx -> {
                tx.run(cypher, params);
                return null;
            });
        } catch (Exception e) {
            log.error("Error deleting nodes with property {}={}: {}", propertyName, propertyValue, e.getMessage());
            throw new RuntimeException("Failed to delete nodes with property " + propertyName + "=" + propertyValue, e);
        }
    }

	@Override
    public void close() {
        driver.close();
    }
}
