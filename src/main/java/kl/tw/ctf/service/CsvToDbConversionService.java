package kl.tw.ctf.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import kl.tw.ctf.dao.DataFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsvToDbConversionService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CsvToDbConversionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createAndPopulateTable(DataFile dataFile) {
        createTable(dataFile);
        populateTableWithData(dataFile);
    }

    private void createTable(DataFile dataFile) {
        String commaSeparatedColumns = String.join(" varchar(225),", dataFile.getColumnNames()).concat(" varchar(225)");
        String sqlStatement =
            "CREATE TABLE IF NOT EXISTS " + dataFile.getTableName() + " "
                + "(id SERIAL NOT NULL PRIMARY KEY,"
                + commaSeparatedColumns + ")";

        jdbcTemplate.execute(sqlStatement);
        System.out.println("DB Upload completed!");

    }


    private void populateTableWithData(DataFile dataFile) {
        dataFile.getColumnValues().forEach(column -> {
            column = column.stream().map(el -> "\'" + el + "\'").collect(toList());

            String query =
                " insert into " + dataFile.getTableName() +
                    "(" + String.join(",", dataFile.getColumnNames()) + ")" +
                    " values (" +  String.join("," , column) + ")";
            jdbcTemplate.execute(query);

        });

    }
}
