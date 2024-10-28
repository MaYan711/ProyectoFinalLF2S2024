%%

%class SQLLexer
%public
%unicode
%line
%column
%caseless
%standalone
%type Token


%{

    private int fila = 1;
    private int columna = 1;


    public Token generarToken(String tipo) {
        return new Token(tipo, yytext(), fila, columna);
    }


    public void incrementarFila() {
        fila++;
        columna = 1;
    }
%}


DIGIT = [0-9]
LETTER = [a-zA-Z_]
STRING = \'[^\']*\'

%eofval{
	return generarToken("FINDELTEXTO");
%eofval}

%%

<YYINITIAL> {

    ("CREATE")               { return generarToken("CREATE"); }
    "TABLE"                { return generarToken("TABLE"); }
    "DATABASE"             { return generarToken("DATABASE"); }
    "INSERT"               { return generarToken("INSERT"); }
    "INTO"                 { return generarToken("INTO"); }
    "VALUES"               { return generarToken("VALUES"); }
    "SELECT"               { return generarToken("SELECT"); }
    "FROM"                 { return generarToken("FROM"); }
    "WHERE"                { return generarToken("WHERE"); }


    "INTEGER"|"BIGINT"      { return generarToken("TIPO_DATO"); }
    "VARCHAR"|"DECIMAL"     { return generarToken("TIPO_DATO"); }
    "DATE"|"TEXT"|"BOOLEAN" { return generarToken("TIPO_DATO"); }


    {DIGIT}+               { return generarToken("ENTERO"); }
    {DIGIT}+"."{DIGIT}+    { return generarToken("DECIMAL"); }
    {STRING}               { return generarToken("CADENA"); }
    "TRUE"|"FALSE"         { return generarToken("BOOLEANO"); }


    "("                    { return generarToken("PARENTESIS_IZQUIERDO"); }
    ")"                    { return generarToken("PARENTESIS_DERECHO"); }
    ","                    { return generarToken("COMA"); }
    ";"                    { return generarToken("PUNTO_Y_COMA"); }
    "."                    { return generarToken("PUNTO"); }
    "="                    { return generarToken("IGUAL"); }
    "+"|"-"|"*"|"/"        { return generarToken("OPERADOR_ARITMETICO"); }
    "<"|">"|"<="|">="      { return generarToken("OPERADOR_RELACIONAL"); }
    "AND"|"OR"|"NOT"       { return generarToken("OPERADOR_LOGICO"); }


    [\t ]+                 { /* Ignorar espacios */ columna += yylength(); }
    \n                     { incrementarFila(); }


    .                      { System.err.println("Caracter desconocido: " + yytext()); }
}