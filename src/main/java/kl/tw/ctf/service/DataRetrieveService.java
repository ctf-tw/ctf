package kl.tw.ctf.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        Map<String, List<Object>> userData = new HashMap<>();

        rowResults.forEach(row -> row.entrySet().forEach((entry -> {
            if (userData.containsKey(entry.getKey())) {
                userData.get(entry.getKey()).add(entry.getValue());
            } else {
                List<Object> values = new ArrayList<>();
                values.add(entry.getValue());
                userData.put(entry.getKey(), values);
            }
        })));

        userData.remove("user_id");
        return buildGraphForData(userData, dataFile.getTableName(), id);
    }

    private String buildGraphForData(Map<String, List<Object>> userData, String tableName, String userId) {
        Map<String, List<MatchHolder>> typeToMatchedUsers = new HashMap<>();
        userData.entrySet().forEach(entry -> {
            String column = entry.getKey();

            entry.getValue().forEach(value -> {
                String sql =
                    "SELECT user_id FROM " + tableName
                        + " WHERE " + column + " = (\'" + value.toString() + "\') "
                        + "AND user_id <> \'" + userId + "\'";
                List<String> userIds = jdbcTemplate.query(sql, (rs, row) -> rs.getString("user_id"));
                if (!userIds.isEmpty()) {
                    if (typeToMatchedUsers.containsKey(column)) {
                        typeToMatchedUsers.get(column).add(new MatchHolder(value.toString(), userIds));
                    } else {
                        List<MatchHolder> matchHolders = new ArrayList<>();
                        matchHolders.add(new MatchHolder(value.toString(), userIds));
                        typeToMatchedUsers.put(column, matchHolders);
                    }
                }
            });

        });
        return convertToJsonResponse(userData, typeToMatchedUsers);
    }

    private String convertToJsonResponse(Map<String, List<Object>> userTypeToValues, Map<String, List<MatchHolder>> matchedUsers) {
        Gson gson = new Gson();

        List<Node> nodes = new ArrayList<>();
        userTypeToValues.entrySet().forEach(entry -> {
            entry.getValue().forEach(value -> {
                String type = entry.getKey();
                nodes.add(new Node((type + value.toString()).hashCode(), value.toString()));
            });
        });

        List<Edge> edges = new ArrayList<>();

        matchedUsers.entrySet().forEach(entry -> {
            entry.getValue().forEach(matchHolder -> {
                matchHolder.userIds.forEach(userTargetId -> {
                    String type = entry.getKey();
                    edges.add(new Edge((type + matchHolder.matchedValue).hashCode(), Integer.parseInt(userTargetId), matchHolder.matchedValue));
                });

            });
        });

        matchedUsers.values().stream().flatMap(Collection::stream)
            .map(matchHolder -> matchHolder.userIds).flatMap(Collection::stream)
            .collect(toSet())
            .stream().map(id -> nodes.add(new Node(Integer.parseInt(id), "user")));


        return gson.toJson(new Graph(nodes, edges));

    }

    static class Graph {
        List<Node> nodes;
        List<Edge> edges;

        public Graph(List<Node> nodes, List<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    static class Node {
        Integer id;
        String type;

        public Node (Integer id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return id + " "  + type;
        }
    }

    static class Edge {
        Integer source;
        Integer target;
        String value;

        public Edge(Integer source, Integer target, String value) {
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
