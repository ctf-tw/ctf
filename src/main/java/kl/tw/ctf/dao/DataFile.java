package kl.tw.ctf.dao;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;


public class DataFile {
    @NonNull String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<String>> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<List<String>> columnValues) {
        this.columnValues = columnValues;
    }

    @NonNull List<String> columnNames;
    @NonNull List<List<String>> columnValues;

    public DataFile(String name, List<String> columnNames, List<List<String>> columnValues) {
        this.name = name;
        this.columnNames = columnNames;
        this.columnValues = columnValues;

    }

}
