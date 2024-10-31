package com.mycompany.analexsin;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String name; 
    private List<Column> columns;

    
    public Table(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    
    public void addColumn(Column column) {
        columns.add(column);
    }

   
    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    
    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }
}
