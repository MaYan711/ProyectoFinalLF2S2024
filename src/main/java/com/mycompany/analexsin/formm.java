package com.mycompany.analexsin;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class formm extends javax.swing.JFrame {

    private JTextPane textArea; // Cambiado a JTextPane para permitir estilos
    private JTextArea lineNumberArea;
    private JLabel positionLabel;
    private JButton analyzeButton;
    private JButton graphButton;
    private JButton reportButton;
    private JButton highlightButton; // Nuevo botón de Resaltar
    private JScrollPane scrollPane;

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

        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
        });

        positionLabel = new JLabel("Fila 1, Columna 1");
        textArea.addCaretListener(e -> updateCursorPosition());

        analyzeButton = new JButton("SINTACTICO");
        analyzeButton.addActionListener(e -> analyzeSQL());
        graphButton = new JButton("GENERAR GRAFICA");
        graphButton.addActionListener(e -> graphSQL());
        reportButton = new JButton("REPORTES");
        reportButton.addActionListener(e -> reportSQL());
        highlightButton = new JButton("PINTAR");
        highlightButton.addActionListener(e -> highlightSyntax());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(positionLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeButton);
        buttonPanel.add(graphButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(highlightButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
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
                parseCreateTable(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");
            } else if (line.toUpperCase().startsWith("INSERT INTO")) {
                parseInsert(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");
            } else if (line.toUpperCase().startsWith("SELECT")) {
                parseSelect(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");
            } else if (line.toUpperCase().startsWith("UPDATE")) {
                parseUpdate(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");
            } else if (line.toUpperCase().startsWith("DELETE FROM")) {
                parseDelete(line);
                analysisResult.append("Sentencia válida: ").append(line).append("\n");
            } else {
                analysisResult.append("Sentencia no reconocida: ").append(line).append("\n");
            }
        } catch (Exception e) {
            analysisResult.append("Error en la sentencia: ").append(line).append("\n").append(e.getMessage()).append("\n");
        }
    }

    JOptionPane.showMessageDialog(this, analysisResult.toString(), "Resultado del Análisis", JOptionPane.INFORMATION_MESSAGE);
}

private void parseCreateDatabase(String line) throws Exception {
    if (!line.matches("CREATE DATABASE [a-zA-Z_][a-zA-Z0-9_]*;")) {
        throw new Exception("Sintaxis incorrecta para creación de base de datos.");
    }
    // Aquí puedes implementar la lógica para crear la base de datos.
}

private void parseCreateTable(String line) throws Exception {
    if (!line.matches("CREATE TABLE [a-zA-Z_][a-zA-Z0-9_]* \\(.*\\);")) {
        throw new Exception("Sintaxis incorrecta para creación de tabla.");
    }
    // Aquí puedes implementar la lógica para crear la tabla.
}

private void parseInsert(String line) throws Exception {
    if (!line.matches("INSERT INTO [a-zA-Z_][a-zA-Z0-9_]* \\(.*\\) VALUES \\(.*\\);")) {
        throw new Exception("Sintaxis incorrecta para inserción.");
    }
    // Aquí puedes implementar la lógica para insertar datos.
}

private void parseSelect(String line) throws Exception {
    if (!line.matches("SELECT (\\*|[a-zA-Z_][a-zA-Z0-9_]*(,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*) FROM [a-zA-Z_][a-zA-Z0-9_]*( WHERE .*|);")) {
        throw new Exception("Sintaxis incorrecta para selección.");
    }
    // Aquí puedes implementar la lógica para seleccionar datos.
}

private void parseUpdate(String line) throws Exception {
    if (!line.matches("UPDATE [a-zA-Z_][a-zA-Z0-9_]* SET [a-zA-Z_][a-zA-Z0-9_]* = .*;")) {
        throw new Exception("Sintaxis incorrecta para actualización.");
    }
    // Aquí puedes implementar la lógica para actualizar datos.
}

private void parseDelete(String line) throws Exception {
    if (!line.matches("DELETE FROM [a-zA-Z_][a-zA-Z0-9_]*;")) {
        throw new Exception("Sintaxis incorrecta para eliminación.");
    }
    // Aquí puedes implementar la lógica para eliminar datos.
}


    private void graphSQL() {
        String text = textArea.getText();
        JOptionPane.showMessageDialog(this, "Graficando SQL...");
    }

    private void reportSQL() {
       String text = textArea.getText();
    JOptionPane.showMessageDialog(this, "Generando reportes SQL...");
    
    // Analizar léxicamente el texto para generar tokens
    List<Token> lexicalTokens = analyzeLexical(text);
    List<Token> syntaxTokens = analyzeSyntax(text);
    List<Token> tablesFound = findTables(text);
    List<Token> modifiedTables = findModifiedTables(text);
    Map<String, Integer> operationCount = countOperations(text);

    // Crear el archivo HTML
    try (FileWriter writer = new FileWriter("C:\\Users\\LL829\\Desktop\\PRUEBAHTML\\reporteSQL.html")) {
        writer.write("<html><body>");

        // Reporte Léxico
        writer.write("<h2>Reporte Léxico</h2>");
        writer.write("<table border='1'><tr><th>Token</th><th>Línea</th><th>Columna</th><th>Descripción</th></tr>");
        for (Token token : lexicalTokens) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td><td>" + token.getDescription() + "</td></tr>");
        }
        writer.write("</table><br>");

        // Reporte Sintáctico
        writer.write("<h2>Reporte Sintáctico</h2>");
        writer.write("<table border='1'><tr><th>Token</th><th>Tipo Token</th><th>Línea</th><th>Columna</th><th>Descripción</th></tr>");
        for (Token token : syntaxTokens) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getType() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td><td>" + token.getDescription() + "</td></tr>");
        }
        writer.write("</table><br>");

        // Tablas Encontradas
        writer.write("<h2>Tablas Encontradas</h2>");
        writer.write("<table border='1'><tr><th>Nombre Tabla</th><th>Línea</th><th>Columna</th></tr>");
        for (Token token : tablesFound) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td></tr>");
        }
        writer.write("</table><br>");

        // Tablas Modificadas y Tipo de Modificación
        writer.write("<h2>Tablas Modificadas y Tipo de Modificación</h2>");
        writer.write("<table border='1'><tr><th>Nombre Tabla</th><th>Tipo Modificación</th><th>Línea</th><th>Columna</th></tr>");
        for (Token token : modifiedTables) {
            writer.write("<tr><td>" + token.getValue() + "</td><td>" + token.getType() + "</td><td>" + token.getLine() + "</td><td>" + token.getColumn() + "</td></tr>");
        }
        writer.write("</table><br>");

        // Número de Operaciones por Sección
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

