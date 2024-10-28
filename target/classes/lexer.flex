/* Nombre de la clase generada */
%class SQLLexer
%public
%unicode
%line
%column

%{
    // Variables de control de posición
    private int fila = 1;
    private int columna = 1;

    // Método para manejar el token generado
    public Token generarToken(String tipo) {
        return new Token(tipo, yytext(), fila, columna);
    }

    // Aumenta la fila en caso de salto de línea
    public void incrementarFila() {
        fila++;
        columna = 1;
    }
%}

// Definición de patrones
DIGIT = [0-9]
LETTER = [a-zA-Z_]
STRING = \'[^\']*\'

// Zona de reglas léxicas
%%
<YYINITIAL> {
    // Palabras clave de SQL
    "CREATE"               { return generarToken("CREATE"); }
    "TABLE"                { return generarToken("TABLE"); }
    "DATABASE"             { return generarToken("DATABASE"); }
    "INSERT"               { return generarToken("INSERT"); }
    "INTO"                 { return generarToken("INTO"); }
    "VALUES"               { return generarToken("VALUES"); }
    "SELECT"               { return generarToken("SELECT"); }
    "FROM"                 { return generarToken("FROM"); }
    "WHERE"                { return generarToken("WHERE"); }

    // Tipos de datos SQL
    "INTEGER"|"BIGINT"     { return generarToken("TIPO_DATO"); }
    "VARCHAR"|"DECIMAL"    { return generarToken("TIPO_DATO"); }
    "DATE"|"TEXT"|"BOOLEAN"{ return generarToken("TIPO_DATO"); }

    // Otros tokens
    {DIGIT}+               { return generarToken("ENTERO"); }
    {DIGIT}+"."{DIGIT}+    { return generarToken("DECIMAL"); }
    {STRING}               { return generarToken("CADENA"); }
    "TRUE"|"FALSE"         { return generarToken("BOOLEANO"); }

    // Signos y operadores
    "("                    { return generarToken("PARENTESIS_IZQUIERDO"); }
    ")"                    { return generarToken("PARENTESIS_DERECHO"); }
    ","                    { return generarToken("COMA"); }
    ";"                    { return generarToken("PUNTO_Y_COMA"); }
    "."                    { return generarToken("PUNTO"); }
    "="                    { return generarToken("IGUAL"); }
    "+"|"-"|"*"|"/"        { return generarToken("OPERADOR_ARITMETICO"); }
    "<"|">"|"<="|">="      { return generarToken("OPERADOR_RELACIONAL"); }
    "AND"|"OR"|"NOT"       { return generarToken("OPERADOR_LOGICO"); }

    // Espacios en blanco y saltos de línea
    [\t ]+                 { /* Ignorar espacios */ columna += yylength(); }
    \n                     { incrementarFila(); }

    // Carácter desconocido
    .                      { System.err.println("Caracter desconocido: " + yytext()); }
}
