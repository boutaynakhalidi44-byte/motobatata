package motobatata.gui;

import java.awt.*;
import javax.swing.*;

public class CodeEditorPanel extends JPanel {

    private JTextArea editor;
    private JLabel statusLabel;

    public CodeEditorPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Code Assembleur 6809"));
        setBackground(new Color(210,170,180));

        // Editor area
        editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setBackground(new Color(245,230,235));
        editor.setCaretColor(Color.BLACK);

        editor.setText(
            "; Exemple\n" +
            "LDA #$10\n" +
            "ADDA #$05\n" +
            "STA $20\n"
        );

        // Status bar at bottom
        statusLabel = new JLabel("Code ready to compile");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(new JScrollPane(editor), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public String getCode() {
        return editor.getText();
    }

    public String[] getCodeLines() {
        return editor.getText().split("\n");
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public void clearEditor() {
        editor.setText("");
        setStatus("Editor cleared");
    }
}