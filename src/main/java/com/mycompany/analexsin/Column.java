package com.mycompany.analexsin;
import java.util.ArrayList;

import java.util.List;


public class Column {
    private String name;         
    private String type;        
    private boolean isPrimaryKey; 

    
    public Column(String name, String type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
    }

    
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    
    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                '}';
    }
}
