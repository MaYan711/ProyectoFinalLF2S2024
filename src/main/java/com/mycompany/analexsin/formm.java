package com.mycompany.analexsin;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

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

        analyzeButton = new JButton("Analizar");
        analyzeButton.addActionListener(e -> analyzeSQL());
        graphButton = new JButton("Generar Gráfico");
        graphButton.addActionListener(e -> graphSQL());
        reportButton = new JButton("Reportes");
        reportButton.addActionListener(e -> reportSQL());

        highlightButton = new JButton("Resaltar");
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
        JOptionPane.showMessageDialog(this, "Analizando SQL...");
    }

    private void graphSQL() {
        String text = textArea.getText();
        JOptionPane.showMessageDialog(this, "Graficando SQL...");
    }

    private void reportSQL() {
        String text = textArea.getText();
        JOptionPane.showMessageDialog(this, "Reportes SQL...");
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
        
        if (index + length <= text.length()) { // Verifica que los índices estén dentro de los límites
            if (token.matches("CREATE|DATABASE|TABLE|KEY|NULL|PRIMARY|UNIQUE|FOREIGN|REFERENCES|ALTER|ADD|COLUMN|TYPE|DROP|CONSTRAINT|IF|EXIST|CASCADE|ON|DELETE|SET|UPDATE|INSERT|INTO|VALUES|SELECT|FROM|WHERE|AS|GROUP|ORDER|BY|ASC|DESC|LIMIT|JOIN")) {
                doc.setCharacterAttributes(index, length, keywordStyle, true);
            } else if (token.matches("INTEGER|BIGINT|VARCHAR|DECIMAL|NUMERIC|DATE|TEXT|BOOLEAN|SERIAL")) {
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
        index += length + 1; // Incrementa la posición actual incluyendo espacio
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

