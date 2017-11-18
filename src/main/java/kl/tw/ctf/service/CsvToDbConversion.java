package kl.tw.ctf.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsvToDbConversion {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CsvToDbConversion(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedDelay = 1000L)
    public void test() {
        String fileName = "hello";
        List<String> columns = asList("user_id", "hello", "world", "name");
        List<String> column1 = asList("1", "30", "40", "John");
        List<String> column2 = asList("2", "50", "60", "Huan");
        createAndPopulateTable(fileName, columns, asList(column1, column2));
    }

    public void createAndPopulateTable(String fileName, List<String> columns, List<List<String>> data) {
        createTable(fileName, columns);
        populateTableWithData(fileName, data);
    }

    private void createTable(String filename, List<String> columns) {
        String commaSeparatedColumns = String.join(" varchar(225),", columns.subList(1, columns.size())).concat(" varchar(225)");
        String sqlStatement =
            "CREATE TABLE IF NOT EXISTS " + filename + " "
                + "(" + columns.get(0) + " " + "SERIAL NOT NULL PRIMARY KEY,"
                + commaSeparatedColumns + ")";

        jdbcTemplate.execute(sqlStatement);
        System.out.println("DB Upload completed!");

    }


    private void populateTableWithData(String fileName, List<List<String>> columnData) {
        columnData.forEach(column -> {
            Long userId = Long.parseLong(column.get(0));
            column = column.stream().map(el -> "\'" + el + "\'").collect(toList());

            String query =
                " insert into " + fileName +
                    " values ( " + userId + ", " + String.join(", " , column.subList(1, column.size())) + ")";
            System.out.println("query to execute " + query);
            jdbcTemplate.execute(query);

        });

    }
}
