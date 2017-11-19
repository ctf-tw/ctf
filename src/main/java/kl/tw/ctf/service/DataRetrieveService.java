package kl.tw.ctf.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import kl.tw.ctf.dao.DataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataRetrieveService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataRetrieveService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getJsonGraphFor(String id, DataFile dataFile) {
        String columns = String.join(",", dataFile.getColumnNames());
        String query = "SELECT " +
            columns + " "
            + "FROM " + dataFile.getTableName() + " "
            + "WHERE user_id = \'" + id + "\'";

        List<Map<String, Object>> rowResults = jdbcTemplate.queryForList(query);

        Set<Node> userNodes = new HashSet<>();
        addNodesToSet(rowResults, userNodes);

        return buildGraphForData(userNodes, dataFile);
    }

    private String buildGraphForData(Set<Node> nodes, DataFile dataFile) {
        String columns = String.join(",", dataFile.getColumnNames());
        String condition = ("SELECT " + columns + " FROM " + dataFile.getTableName()) + " WHERE " + " ";
        for (String columnName : dataFile.getColumnNames()) {
            String preparation =
                "('" + String
                    .join("','", nodes.stream().filter(el -> el.type.equals(columnName)).map(el -> el.value).collect(
                        toList())) + "')";
            String prep = columnName + " in " + preparation + " OR ";
            condition += prep;
        }
        condition = condition.substring(0, condition.length() - 3);

        List<Map<String, Object>> rowResults = jdbcTemplate.queryForList(condition);
        addNodesToSet(rowResults, nodes);

        HashMap<String, List<Node>> map = new HashMap<>();
        nodes.forEach(node -> {
            String key = node.type + " | " +  node.value;
            if (map.containsKey(key)) {
                map.get(key).add(node);
            }
            else {
                List<Node> nodes1 = new ArrayList<>();
                nodes1.add(node);
                map.put(key, nodes1);
            }
        });

        Set<Edge> edges = convertToEdges(map);

        return convertToJson(selectMatchedNodes(edges, nodes), edges);
    }


    private Set<Node> selectMatchedNodes(Set<Edge> edges, Set<Node> nodes) {
        Set<Node> results = new HashSet<>();
        edges.forEach(edge -> {
            nodes.forEach(node -> {
                if (Objects.equals(node.uniqueId(), edge.target) || Objects.equals(node.uniqueId(), edge.source)) {
                    results.add(node);
                }
            });
        });
        return results;
    }

    private Set<Edge> convertToEdges(Map<String, List<Node>> dataMap) {
        Set<Edge> edges = new HashSet<>();
        dataMap.entrySet().forEach(entry -> {
            if (entry.getValue().size() > 1) {
                List<Node> nodes = entry.getValue();
                nodes.forEach(node -> {
                    String key1 = node.value + node.type;
                    String key2 = node.id;
                    edges.add(new Edge(key1, key2, "1"));
                });
            }
        });
        return edges;
    }

    private void addNodesToSet(List<Map<String, Object>> rowResults, Set<Node> nodes) {
        rowResults.forEach(row -> {
            String  currentUserId = null;
            int count = 0;
            for (Map.Entry<String, Object> entry1: row.entrySet()) {
                Node node;
                if (count == 0) {
                    count++;
                    currentUserId = entry1.getValue().toString();
                    String type = entry1.getKey();
                    node = new Node(currentUserId, type, currentUserId);
                }
                else {
                    String type = entry1.getKey();
                    String value1 = entry1.getValue().toString();
                    node = new Node(currentUserId, type, value1);
                }
                nodes.add(node);
            }
        });
    }

    private String convertToJson(Set<Node> nodes, Set<Edge> edges) {
        Gson gson = new Gson();
        Set<RenderNode> nodesToRender = nodes.stream().map(node -> new RenderNode(node.uniqueId(), node.type, node.value))
            .collect(toSet());

        Graph graph = new Graph(nodesToRender, edges);
        return gson.toJson(graph);


    }

    static class RenderNode {
        String id;
        String type;
        String value;

        public RenderNode(String id, String type, String value) {
            this.id = id;
            this.type = type;
            this.value = value;
        }
    }

    static class Graph {
        Set<RenderNode> nodes;
        Set<Edge> edges;

        public Graph(Set<RenderNode> nodes, Set<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    static class Node {
        String id;
        String type;
        String value;

        public Node (String id, String type, String value) {
            this.id = id;
            this.type = type;
            this.value = value;
        }

        public String uniqueId() {
            if (Objects.equals(type, "user_id")) {
                return id;
            }
            return value + type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node)
                return ((Node) obj).uniqueId().equals(this.uniqueId()) && ((Node) obj).type.equals(this.type) && ((Node) obj).value.equals(this.value);
            return false;
        }

        @Override
        public int hashCode() {
            return (this.id).hashCode() + (this.value).hashCode() * 32 + (this.type).hashCode() * 32 * 32 ;
        }

        @Override
        public String toString() {
            return id + " "  + type;
        }
    }

    static class Edge {
        String source;
        String target;
        String value;

        public Edge(String source, String target, String value) {
            this.source = source;
            this.target = target;
            this.value = value;
        }

        @Override
        public String toString() {
            return source + " " + target + " " + value;
        }
    }

    static class MatchHolder {
        String matchedValue;
        List<String> userIds;

        public MatchHolder(String data, List<String> userIds) {
            this.matchedValue = data;
            this.userIds = userIds;
        }

        @Override
        public String toString() {
            return matchedValue + " " + userIds;
        }
    }
}
