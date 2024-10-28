
package com.mycompany.analexsin;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class formm extends javax.swing.JFrame {

    private JTextArea textArea;
    private JTextArea lineNumberArea;
    private JLabel positionLabel;
    private JButton analyzeButton;
    private JButton graphButton;
    private JButton reportButton;
    private JScrollPane scrollPane;
    
    public formm() {
       // initComponents();
        setTitle("Editor de SQL");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        textArea = new JTextArea(20, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lineNumberArea = new JTextArea("1 ");
        lineNumberArea.setEditable(false);
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberArea);

        
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
            public void changedUpdate(DocumentEvent e) { updateLineNumbers(); }
        });
        positionLabel = new JLabel("Fila 1, Columna 1");
        textArea.addCaretListener(e -> updateCursorPosition());

        
        analyzeButton = new JButton("Analizar");
        analyzeButton.addActionListener(e -> analyzeSQL());
        graphButton = new JButton("Generar Gráfico");
        graphButton.addActionListener(e -> graphSQL());
        reportButton = new JButton("Reportes");
        reportButton.addActionListener(e -> reportSQL());

        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(positionLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeButton);
        buttonPanel.add(graphButton);
        buttonPanel.add(reportButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void updateLineNumbers() {
        int totalLines = textArea.getLineCount();
        StringBuilder lineNumbers = new StringBuilder("1\n");
        for (int i = 2; i <= totalLines; i++) {
            lineNumbers.append(i).append("\n");
        }
        lineNumberArea.setText(lineNumbers.toString());
    }

    private void updateCursorPosition() {
        try {
            int caretPos = textArea.getCaretPosition();
            int row = textArea.getLineOfOffset(caretPos) + 1;
            int col = caretPos - textArea.getLineStartOffset(row - 1) + 1;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new formm().setVisible(true));
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formm().setVisible(true);
            }
        });
    }
    }

        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

