package com.mycompany.analexsin;

public class Token {
    private String type;
    private String value;
    private int line;
    private int column;
    private String description; // Nueva propiedad para descripción del token
    
    

    public Token(String type, String value, int line, int column, String description) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.description = description;
        
    }
    

    Token(String tipo, String yytext, int fila, int columna) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Getters y setters
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
    
}
