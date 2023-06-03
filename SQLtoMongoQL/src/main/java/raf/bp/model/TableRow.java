package raf.bp.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TableRow {
    private String name;
    private Map<String, Object> fields;


    public TableRow() {
        this.fields = new HashMap<>();
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
