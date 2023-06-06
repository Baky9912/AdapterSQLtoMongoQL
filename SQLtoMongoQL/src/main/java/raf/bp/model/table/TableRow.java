package raf.bp.model.table;

import lombok.Data;

import java.util.*;

@Data
public class TableRow {
    private String name;
    private Map<String, Object> fields;


    public TableRow() {
        this.fields = new LinkedHashMap<>();
    }

    public void addField(String fieldName, Object value) {
        this.fields.put(fieldName, value);

    }

    @Override
    public String toString() {
        return "TableRow{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}
