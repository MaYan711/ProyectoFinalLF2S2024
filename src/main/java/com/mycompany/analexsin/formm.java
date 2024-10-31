package com.mycompany.analexsin;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;


public class formm extends javax.swing.JFrame {

    private JTextPane textArea; 
    private JTextArea lineNumberArea;
    private JLabel positionLabel;
    private JScrollPane scrollPane;
    private JButton analyzeButton, graphButton, reportButton, highlightButton;
    private String currentFilePath; 

    public formm() {
        setTitle("Editor de SQL");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextPane();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lineNumberArea = new JTextArea("1 ");
        lineNumberArea.setEditable(false);
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberArea);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateLineNumbers();
            }
        });

        positionLabel = new JLabel("Fila 1, Columna 1");
        textArea.addCaretListener(e -> updateCursorPosition());

        analyzeButton = new JButton("SINTACTICO");
        analyzeButton.addActionListener(e -> analyzeSQL());
        graphButton = new JButton("GENERAR GRAFICA");
        graphButton.addActionListener(e -> graphSQL());
        reportButton = new JButton("REPORTES");
        reportButton.addActionListener(e -> reportSQL());
        highlightButton = new JButton("LEXICO");
        highlightButton.addActionListener(e -> highlightSyntax());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(positionLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeButton);
        buttonPanel.add(graphButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(highlightButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Crear el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        
        // Opciones de menú
        JMenuItem newItem = new JMenuItem("Nuevo");
        newItem.addActionListener(e -> createNewFile());
        JMenuItem loadItem = new JMenuItem("Cargar");
        loadItem.addActionListener(e -> loadFile());
        JMenuItem saveItem = new JMenuItem("Guardar");
        saveItem.addActionListener(e -> saveFile());
        JMenuItem saveAsItem = new JMenuItem("Guardar Como");
        saveAsItem.addActionListener(e -> saveFileAs());

        fileMenu.add(newItem);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar); // Establecer la barra de menú

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void createNewFile() {
        // Limpiar el área de texto y restablecer la ruta del archivo actual
        textArea.setText("");
        currentFilePath = null; // No hay archivo guardado actualmente
    }
    
    
      private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFilePath = file.getPath(); // Guardar la ruta del archivo actual
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.setText(""); // Limpiar el área de texto antes de cargar
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.setText(textArea.getText() + line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
     private void saveFile() {
        if (currentFilePath != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFilePath))) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            saveFileAs(); // Si no hay archivo actual, usar "Guardar Como"
        }
    }
    
     private void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFilePath = file.getPath(); // Guardar la ruta del nuevo archivo
            saveFile(); // Llamar al método de guardar
        }
    }

    private void updateLineNumbers() {
        int totalLines = textArea.getDocument().getDefaultRootElement().getElementCount();
        StringBuilder lineNumbers = new StringBuilder("1\n");
        for (int i = 2; i <= totalLines; i++) {
            lineNumbers.append(i).append("\n");
        }
        lineNumberArea.setText(lineNumbers.toString());
    }

    private void updateCursorPosition() {
        try {
            int caretPos = textArea.getCaretPosition();
            int row = textArea.getDocument().getDefaultRootElement().getElementIndex(caretPos) + 1;
            int col = caretPos - textArea.getDocument().getDefaultRootElement().getElement(row - 1).getStartOffset() + 1;
            positionLabel.setText("Fila " + row + ", Columna " + col);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

 private void analyzeSQL() {
    String text = textArea.getText();
    String[] lines = text.split("\n");
    StringBuilder analysisResult = new StringBuilder();

    boolean insideCreateTable = false; // Para detectar inicio y fin de una tabla
    StringBuilder tableStructure = new StringBuilder(); // Acumula la estructura de CREATE TABLE

    for (String line : lines) {
        line = line.trim();
        if (line.isEmpty()) {
            continue;
        }

        try {
            if (line.toUpperCase().startsWith("CREATE DATABASE")) {
                parseCreateDatabase(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");

            } else if (line.toUpperCase().startsWith("CREATE TABLE")) {
                insideCreateTable = true;
                tableStructure.append(line).append(" ");

            } else if (insideCreateTable) {
                tableStructure.append(line).append(" ");
                
                // Verificar si se cierra la tabla con el paréntesis final
                if (line.endsWith(");")) {
                    parseCreateTable(tableStructure.toString().trim());
                    insideCreateTable = false;
                    tableStructure.setLength(0); // Limpiar para la próxima tabla
                    analysisResult.append("Sentencia CREATE TABLE válida.\n");
                }

            } else if (line.toUpperCase().startsWith("ALTER TABLE")) {
                parseAlterTable(line);
                analysisResult.append("Sentencia ALTER TABLE válida: ").append(line).append("\n");

            } else if (line.toUpperCase().startsWith("DROP TABLE")) {
                parseDropTable(line);
                analysisResult.append("Sentencia DROP TABLE válida: ").append(line).append("\n");

            } else if (line.toUpperCase().startsWith("SELECT")) {
                parseSelect(line);
                analysisResult.append("Sentencia SELECT válida: ").append(line).append("\n");

            } else if (line.toUpperCase().startsWith("UPDATE")) {
                parseUpdate(line);
                analysisResult.append("Sentencia UPDATE válida: ").append(line).append("\n");

            } else if (line.toUpperCase().startsWith("DELETE")) {
                parseDelete(line);
                analysisResult.append("Sentencia DELETE válida: ").append(line).append("\n");

            } else {
                analysisResult.append("Sentencia no reconocida: ").append(line).append("\n");
            }

        } catch (Exception e) {
            analysisResult.append("Error en la sentencia: ").append(line).append("\n").append(e.getMessage()).append("\n");
        }
    }

    JOptionPane.showMessageDialog(this, analysisResult.toString(), "Resultado del Análisis", JOptionPane.INFORMATION_MESSAGE);
}
  
  
  
  
  
  private void parseInsertInto(String line) throws Exception {
    // Valida que la estructura sea `INSERT INTO <identificador> (<columnas>) VALUES (<valores>);`
    if (!line.matches("(?i)^INSERT INTO \\w+ \\(.+\\) VALUES \\(.+\\);?$")) {
        throw new Exception("Error en la sintaxis de INSERT INTO. Asegúrate de usar `INSERT INTO <tabla> (<columna>, ...) VALUES (<valor>, ...);`");
    }
    
}

private void parseSelect(String line) throws Exception {
    if (!line.matches("(?i)^SELECT\\s+.+\\s+FROM\\s+\\w+(\\s+[A-Z]+\\s+.*)?;$")) {
        throw new Exception("Error en la sintaxis de SELECT. Asegúrate de usar `SELECT <columnas> FROM <tabla> [cláusulas opcionales];`");
    }

    String[] clauses = line.split("(?i)(WHERE|GROUP BY|ORDER BY|LIMIT|JOIN)");
    
    if (clauses.length > 0) {
        if (!clauses[0].trim().matches("(?i)^SELECT\\s+.+\\s+FROM\\s+\\w+$")) {
            throw new Exception("Error en la sección SELECT/FROM de la sentencia.");
        }
    }

    for (int i = 1; i < clauses.length; i++) {
        String clause = clauses[i].trim();
        
        if (clause.toUpperCase().startsWith("WHERE")) {
            if (!clause.matches("(?i)^WHERE\\s+.+$")) {
                throw new Exception("Error en la cláusula WHERE.");
            }
        } else if (clause.toUpperCase().startsWith("GROUP BY")) {
            if (!clause.matches("(?i)^GROUP BY\\s+\\w+(\\.\\w+)?$")) {
                throw new Exception("Error en la cláusula GROUP BY.");
            }
        } else if (clause.toUpperCase().startsWith("ORDER BY")) {
            if (!clause.matches("(?i)^ORDER BY\\s+\\w+(\\.\\w+)?\\s*(ASC|DESC)?$")) {
                throw new Exception("Error en la cláusula ORDER BY.");
            }
        } else if (clause.toUpperCase().startsWith("LIMIT")) {
            if (!clause.matches("(?i)^LIMIT\\s+\\d+$")) {
                throw new Exception("Error en la cláusula LIMIT.");
            }
        } else if (clause.toUpperCase().startsWith("JOIN")) {
            if (!clause.matches("(?i)^JOIN\\s+\\w+\\s+ON\\s+\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+$")) {
                throw new Exception("Error en la cláusula JOIN.");
            }
        } else {
            throw new Exception("Cláusula no reconocida en la sentencia SELECT.");
        }
    }
}





private void parseCreateTable(String structure) throws Exception {
    if (!structure.matches("(?i)^CREATE TABLE \\w+ \\(.+\\);$")) {
        throw new Exception("Error en la sintaxis de CREATE TABLE. Asegúrate de usar `CREATE TABLE <tabla> (<columna> <tipo>, ...);`");
    }
    
    String tableName = structure.split("\\s+")[2];
    if (tableName.isEmpty()) {
        throw new Exception("Error: Falta el nombre de la tabla.");
    }
    
    String columnsDefinition = structure.substring(structure.indexOf("(") + 1, structure.lastIndexOf(")"));
    String[] columns = columnsDefinition.split(",");
    
    for (String column : columns) {
        String trimmedColumn = column.trim();
        if (!trimmedColumn.matches("\\w+\\s+(SERIAL|INTEGER|BIGINT|VARCHAR\\(\\d+\\)|DECIMAL\\(\\d+,\\d+\\)|NUMERIC\\(\\d+,\\d+\\)|DATE|TEXT|BOOLEAN)(\\s+PRIMARY KEY|\\s+NOT NULL|\\s+UNIQUE)?")) {
            throw new Exception("Error en la definición de columna: `" + trimmedColumn + "` no sigue una estructura válida.");
        }
    }
}





private void parseCreateDatabase(String line) throws Exception {
    if (!line.matches("(?i)^CREATE DATABASE \\w+;$")) {
        throw new Exception("Error en la sintaxis de CREATE DATABASE. Debe seguir la estructura `CREATE DATABASE <identificador>;`");
    }
    String dbName = line.split("\\s+")[2].replace(";", "");
    if (dbName.isEmpty()) {
        throw new Exception("Error: Falta el nombre de la base de datos.");
    }
}

private void parseColumn(String column) throws Exception {
    String columnPattern = "^[a-zA-Z_][a-zA-Z0-9_]*\\s+(SERIAL|INTEGER|BIGINT|VARCHAR\\(\\d+\\)|DECIMAL\\(\\d+,\\d+\\)|NUMERIC\\(\\d+,\\d+\\)|DATE|TEXT|BOOLEAN)(\\s+(PRIMARY KEY|NOT NULL|UNIQUE))?$";
    if (!column.matches(columnPattern)) {
        throw new Exception("Error en la declaración de columna. La estructura esperada es '<identificador> [Tipo_de_dato] [PRIMARY KEY | NOT NULL | UNIQUE]'.");
    }
}

private void parseForeignKey(String line) throws Exception {
    String foreignKeyPattern = "^CONSTRAINT\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+FOREIGN KEY\\s*\\([a-zA-Z_][a-zA-Z0-9_]*\\)\\s+REFERENCES\\s+[a-zA-Z_][a-zA-Z0-9_]*\\([a-zA-Z_][a-zA-Z0-9_]*\\)$";
    if (!line.matches(foreignKeyPattern)) {
        throw new Exception("Error en la declaración de llave foránea. La estructura esperada es 'CONSTRAINT <identificador> FOREIGN KEY (<identificador>) REFERENCES <identificador>(<identificador>)'.");
    }
}


private void parseAlterTable(String line) throws Exception {
    if (!line.matches("(?i)^ALTER TABLE \\w+ (ADD|DROP) COLUMN \\w+;$")) {
        throw new Exception("Error en la sintaxis de ALTER TABLE. Asegúrate de usar `ALTER TABLE <tabla> ADD|DROP COLUMN <columna>;`");
    }
    
    String[] tokens = line.split("\\s+");
    String tableName = tokens[2];
    String columnName = tokens[4];
    
    if (tableName.isEmpty() || columnName.isEmpty()) {
        throw new Exception("Error: Falta el nombre de la tabla o de la columna en ALTER TABLE.");
    }
}


private void parseDropTable(String line) throws Exception {
    if (!line.matches("(?i)^DROP TABLE \\w+;$")) {
        throw new Exception("Error en la sintaxis de DROP TABLE. Debe seguir la estructura `DROP TABLE <identificador>;`");
    }
    
    String tableName = line.split("\\s+")[2].replace(";", "");
    if (tableName.isEmpty()) {
        throw new Exception("Error: Falta el nombre de la tabla.");
    }
}




private boolean isColumnDeclaration(String line) {
    return line.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s+(SERIAL|INTEGER|BIGINT|VARCHAR\\(\\d+\\)|DECIMAL\\(\\d+,\\d+\\)|NUMERIC\\(\\d+,\\d+\\)|DATE|TEXT|BOOLEAN)(\\s+(PRIMARY KEY|NOT NULL|UNIQUE))?$");
}




private void parseInsert(String line) throws Exception {
    if (!line.matches("INSERT INTO [a-zA-Z_][a-zA-Z0-9_]* \\(.*\\) VALUES \\(.*\\);")) {
        throw new Exception("Sintaxis incorrecta para inserción.");
    }
    
}



private void parseUpdate(String line) throws Exception {
    if (!line.matches("(?i)^UPDATE\\s+\\w+\\s+SET\\s+.+$")) {
        throw new Exception("Error en la sintaxis de UPDATE. Asegúrate de usar `UPDATE <tabla> SET <columna> = [valor] [WHERE];`");
    }

    String[] parts = line.split("(?i)SET");
    String setClause = parts[1].trim();

    if (!setClause.matches(".+?=\\s*.+")) {
        throw new Exception("Error en la cláusula SET. Debe tener la forma `<columna> = [valor]`.");
    }

    if (setClause.contains("WHERE")) {
        String[] setParts = setClause.split("WHERE");
        if (setParts.length > 1) {
            String whereClause = setParts[1].trim();
            if (!whereClause.isEmpty()) {
                if (!whereClause.matches(".+")) {
                    throw new Exception("Error en la cláusula WHERE.");
                }
            }
        }
    }
}

private void parseDelete(String line) throws Exception {
    if (!line.matches("(?i)^DELETE\\s+FROM\\s+\\w+(\\s+WHERE\\s+.+)?;$")) {
        throw new Exception("Error en la sintaxis de DELETE. Asegúrate de usar `DELETE FROM <tabla> [WHERE];`");
    }

    if (line.toUpperCase().contains("WHERE")) {
        String[] parts = line.split("(?i)WHERE");
        String whereClause = parts[1].trim();
        if (!whereClause.isEmpty()) {
            if (!whereClause.matches(".+")) {
                throw new Exception("Error en la cláusula WHERE.");
            }
        }
    }
}


    private void graphSQL() {
        String text = textArea.getText();
        JOptionPane.showMessageDialog(this, "Graficando SQL...");
    }

    private void reportSQL() {
       String text = textArea.getText();
    JOptionPane.showMessageDialog(this, "Generando reportes SQL...");
    
    
    List<Token> lexicalTokens = analyzeLexical(text);
    List<Token> syntaxTokens = analyzeSyntax(text);
    List<Token> tablesFound = findTables(text);
    List<Token> modifiedTables = findModifiedTables(text);
    Map<String, Integer> operationCount = countOperations(text);

    
    try (FileWriter writer = new FileWriter("C:\\Users\\LL829\\Desktop\\PRUEBAHTML\\reporteSQL.html")) {
        writer.write("<html><body>");

        
        writer.write("<h2>Reporte Léxico</h2>");
        writer.write("<table border='1'><tr><th>Token</th><th>Línea</th><th>Columna</th><th>Descripción</th></tr>");
        for (Token token : lexicalTokens) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td><td>" + token.getDescription() + "</td></tr>");
        }
        writer.write("</table><br>");

        
        writer.write("<h2>Reporte Sintáctico</h2>");
        writer.write("<table border='1'><tr><th>Token</th><th>Tipo Token</th><th>Línea</th><th>Columna</th><th>Descripción</th></tr>");
        for (Token token : syntaxTokens) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getType() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td><td>" + token.getDescription() + "</td></tr>");
        }
        writer.write("</table><br>");

        
        writer.write("<h2>Tablas Encontradas</h2>");
        writer.write("<table border='1'><tr><th>Nombre Tabla</th><th>Línea</th><th>Columna</th></tr>");
        for (Token token : tablesFound) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td></tr>");
        }
        writer.write("</table><br>");

        
        writer.write("<h2>Tablas Modificadas y Tipo de Modificación</h2>");
        writer.write("<table border='1'><tr><th>Nombre Tabla</th><th>Tipo Modificación</th><th>Línea</th><th>Columna</th></tr>");
        for (Token token : modifiedTables) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getType() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td></tr>");
        }
        writer.write("</table><br>");

        
        writer.write("<h2>Número de Operaciones por Sección</h2>");
        writer.write("<table border='1'><tr><th>Operación</th><th>Count</th></tr>");
        for (Map.Entry<String, Integer> entry : operationCount.entrySet()) {
            writer.write("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
        }
        writer.write("</table><br>");

        writer.write("</body></html>");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al generar el archivo HTML.");
    }
    }
    
    private List<Token> findTables(String text) {
    List<Token> tables = new ArrayList<>();
    Pattern pattern = Pattern.compile("\\bFROM\\s+(\\w+)|\\bJOIN\\s+(\\w+)|\\bINTO\\s+(\\w+)|\\bTABLE\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    String[] lines = text.split("\n");
    for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        matcher = pattern.matcher(line);
        while (matcher.find()) {
            String tableName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            tables.add(new Token("Tabla", tableName, i + 1, matcher.start() + 1, "Tabla encontrada"));
        }
    }
    return tables;
}
private List<Token> findModifiedTables(String text) {
    List<Token> modifiedTables = new ArrayList<>();
    Pattern pattern = Pattern.compile("\\b(UPDATE|DELETE|ALTER|INSERT INTO)\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    String[] lines = text.split("\n");
    for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        matcher = pattern.matcher(line);
        while (matcher.find()) {
            String operation = matcher.group(1);
            String tableName = matcher.group(2);
            modifiedTables.add(new Token(operation, tableName, i + 1, matcher.start() + 1, "Tabla modificada"));
        }
    }
    return modifiedTables;
}
private Map<String, Integer> countOperations(String text) {
    Map<String, Integer> operationCount = new HashMap<>();
    String[] operations = {"CREATE", "DELETE", "UPDATE", "SELECT", "ALTER"};
    
    for (String op : operations) {
        Pattern pattern = Pattern.compile("\\b" + op + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        operationCount.put(op, count);
    }
    
    return operationCount;
}

    
    private List<Token> analyzeLexical(String text) {
    // Implementa aquí tu análisis léxico.
    // Debe devolver una lista de tokens con posibles errores léxicos.
    List<Token> tokens = new ArrayList<>();
    
    // Ejemplo de token léxico no reconocido
    tokens.add(new Token("Desconocido", "<>", 1, 3, "Token no reconocido"));
    
    return tokens;
}

private List<Token> analyzeSyntax(String text) {
    // Implementa aquí tu análisis sintáctico.
    // Debe devolver una lista de tokens con posibles errores sintácticos.
    List<Token> tokens = new ArrayList<>();
    
    // Ejemplo de error sintáctico
    tokens.add(new Token("Identificador", "nombre", 4, 5, "Secuencia de token inválida"));
    
    return tokens;
}


    public void highlightSyntax() {
    String text = textArea.getText();
    StyledDocument doc = textArea.getStyledDocument();
    doc.setCharacterAttributes(0, text.length(), textArea.getStyle(StyleContext.DEFAULT_STYLE), true);
    Style keywordStyle = textArea.addStyle("Keyword", null);
    StyleConstants.setForeground(keywordStyle, Color.ORANGE);
    Style datatypeStyle = textArea.addStyle("Datatype", null);
    StyleConstants.setForeground(datatypeStyle, new Color(128, 0, 128));
    Style numberStyle = textArea.addStyle("Number", null);
    StyleConstants.setForeground(numberStyle, Color.BLUE);
    Style dateStyle = textArea.addStyle("Date", null);
    StyleConstants.setForeground(dateStyle, Color.YELLOW);
    Style stringStyle = textArea.addStyle("String", null);
    StyleConstants.setForeground(stringStyle, Color.GREEN);
    Style identifierStyle = textArea.addStyle("Identifier", null);
    StyleConstants.setForeground(identifierStyle, new Color(255, 0, 255));
    Style booleanStyle = textArea.addStyle("Boolean", null);
    StyleConstants.setForeground(booleanStyle, Color.BLUE);
    Style operatorStyle = textArea.addStyle("Operator", null);
    StyleConstants.setForeground(operatorStyle, Color.BLACK);
    Style logicStyle = textArea.addStyle("Logic", null);
    StyleConstants.setForeground(logicStyle, Color.ORANGE);
    Style commentStyle = textArea.addStyle("Comment", null);
    StyleConstants.setForeground(commentStyle, Color.GRAY);
    String[] tokens = text.split("\\s+");
    int index = 0;

    for (String token : tokens) {
        int length = token.length();
        
        if (index + length <= text.length()) { 
            if (token.matches("CREATE|DATABASE|TABLE|KEY|NULL|PRIMARY|UNIQUE|FOREIGN|REFERENCES|ALTER|ADD|COLUMN|TYPE|DROP|CONSTRAINT|IF|EXIST|CASCADE|ON|DELETE|SET|UPDATE|INSERT|INTO|VALUES|SELECT|FROM|WHERE|AS|GROUP|ORDER|BY|ASC|DESC|LIMIT|JOIN")) {
                doc.setCharacterAttributes(index, length, keywordStyle, true);
            } else if (token.matches("INTEGER |BIGINT|VARCHAR|DECIMAL|NUMERIC|DATE|TEXT|BOOLEAN|SERIAL")) {
                doc.setCharacterAttributes(index, length, datatypeStyle, true);
            } else if (token.matches("[0-9]+")) {
                doc.setCharacterAttributes(index, length, numberStyle, true);
            } else if (token.matches("[0-9]+\\.[0-9]+")) {
                doc.setCharacterAttributes(index, length, numberStyle, true);
            } else if (token.matches("'\\d{4}-\\d{2}-\\d{2}'")) {
                doc.setCharacterAttributes(index, length, dateStyle, true);
            } else if (token.matches("'[^']*'")) {
                doc.setCharacterAttributes(index, length, stringStyle, true);
            } else if (token.matches("[a-zA-Z_]+[a-zA-Z0-9_]*")) {
                doc.setCharacterAttributes(index, length, identifierStyle, true);
            } else if (token.matches("TRUE|FALSE")) {
                doc.setCharacterAttributes(index, length, booleanStyle, true);
            } else if (token.matches("\\(|\\)|,|;|\\.|=|\\+|\\-|\\*|\\/|<|>|<=|>=|SUM|AVG|COUNT|MAX|MIN")) {
                doc.setCharacterAttributes(index, length, operatorStyle, true);
            } else if (token.matches("AND|OR|NOT")) {
                doc.setCharacterAttributes(index, length, logicStyle, true);
            } else if (token.matches("--.*")) {
                doc.setCharacterAttributes(index, length, commentStyle, true);
            }
        }
        index += length + 1; 
    }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new formm().setVisible(true));
    }
}

        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

