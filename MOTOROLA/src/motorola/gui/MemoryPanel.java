package motorola.gui;

import java.awt.*;
import javax.swing.*;
import motorola.memory.Memory;

public class MemoryPanel extends JPanel {

    private final JTextArea area;
    private final JTextField addressField;
    private final Memory memory;
    private int startAddress = 0x0000;
    private static final int ROWS_TO_DISPLAY = 24; // 384 bytes

    public MemoryPanel(Memory memory) {
        this.memory = memory;

        setLayout(new BorderLayout());
        setBorder(Theme.createTitledBorder("MÉMOIRE - LECTURE SEULE (0x0000 - 0xFFFF)"));
        setBackground(Theme.PANEL_LIGHTER);

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Theme.PANEL);
        
        JLabel addrLabel = new JLabel("Aller à l'adresse:");
        addrLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        addrLabel.setForeground(new Color(40, 20, 5));
        addressField = new JTextField("0000", 10);
    addressField.setFont(Theme.FONT_NORMAL);
    // even lighter baby pink background for the address field
    addressField.setBackground(new Color(255, 245, 251));
        addressField.setForeground(new Color(40, 20, 5));
        
        JButton goButton = new JButton("Aller");
        goButton.addActionListener(e -> goToAddress());
        
        // Scroll buttons
        JButton upButton = new JButton("▲ Haut");
        JButton downButton = new JButton("▼ Bas");
        upButton.addActionListener(e -> scrollMemory(-16));
        downButton.addActionListener(e -> scrollMemory(16));
        
        controlPanel.add(addrLabel);
        controlPanel.add(addressField);
        controlPanel.add(goButton);
        controlPanel.add(new JSeparator(JSeparator.VERTICAL));
        controlPanel.add(upButton);
        controlPanel.add(downButton);

        // Text area - READ ONLY
        area = new JTextArea(ROWS_TO_DISPLAY, 60);
    area.setFont(new Font("Monospaced", Font.PLAIN, 11));
        area.setEditable(false);  // Ensure it's completely read-only
    // even lighter baby pink background for the memory dump area
    area.setBackground(new Color(255, 245, 251));
        area.setForeground(new Color(40, 20, 5));
        area.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(area,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        refresh();
    }

    private void goToAddress() {
        try {
            String input = addressField.getText().trim();
            startAddress = Integer.parseInt(input, 16) & 0xFFFF;
            refresh();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format d'adresse invalide (utilisez hexadécimal)");
        }
    }

    private void scrollMemory(int delta) {
        startAddress = (startAddress + delta) & 0xFFFF;
        addressField.setText(String.format("%04X", startAddress));
        refresh();
    }

    public void refresh() {
        StringBuilder sb = new StringBuilder();
        sb.append("Adresse  : 00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F  |ASCII|\n");
        sb.append("─".repeat(76)).append("\n");
        
        for (int i = 0; i < ROWS_TO_DISPLAY; i++) {
            int addr = startAddress + (i * 16);
            if (addr >= 0x10000) break;
            
            sb.append(String.format("%04X     : ", addr));
            
            // Hex display
            for (int j = 0; j < 16; j++) {
                int byteVal = memory.readByte(addr + j) & 0xFF;
                sb.append(String.format("%02X ", byteVal));
                if (j == 7) sb.append(" "); // Mid-row separator
            }
            
            // ASCII display
            sb.append(" |");
            for (int j = 0; j < 16; j++) {
                int byteVal = memory.readByte(addr + j) & 0xFF;
                if (byteVal >= 32 && byteVal < 127) {
                    sb.append((char) byteVal);
                } else {
                    sb.append(".");
                }
            }
            sb.append("|\n");
        }
        
        area.setText(sb.toString());
    }
}
