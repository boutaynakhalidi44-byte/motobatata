package motobatata.gui;

import java.awt.*;
import javax.swing.*;
import motobatata.cpu.CPU;

public class RegisterPanel extends JPanel {

    private final CPU cpu;
    private JLabel aLabel, bLabel, dLabel, xLabel, yLabel, uLabel, sLabel, pcLabel, dpLabel, ccLabel;

    public RegisterPanel(CPU cpu) {
        this.cpu = cpu;

        setLayout(new GridLayout(10, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Registres (Lecture Seule)"));
        setBackground(new Color(190, 150, 160));

        // 8-bit registers
        aLabel = addRegisterDisplay("A (8-bit)");
        bLabel = addRegisterDisplay("B (8-bit)");
        
        // 16-bit combined
        dLabel = addRegisterDisplay("D (A:B)");
        
        // 16-bit index registers
        xLabel = addRegisterDisplay("X (16-bit)");
        yLabel = addRegisterDisplay("Y (16-bit)");
        uLabel = addRegisterDisplay("U (16-bit)");
        sLabel = addRegisterDisplay("S (16-bit)");
        
        // Control registers
        pcLabel = addRegisterDisplay("PC");
        dpLabel = addRegisterDisplay("DP");
        ccLabel = addRegisterDisplay("CC");

        refresh();
    }

    private JLabel addRegisterDisplay(String name) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        nameLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel("00000000");
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        valueLabel.setForeground(new Color(0, 255, 100));
        valueLabel.setOpaque(true);
        valueLabel.setBackground(new Color(30, 60, 40));
        valueLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        add(nameLabel);
        add(valueLabel);

        return valueLabel;
    }

    public void refresh() {
        aLabel.setText(String.format("0x%02X", cpu.getAccA()));
        bLabel.setText(String.format("0x%02X", cpu.getAccB()));
        dLabel.setText(String.format("0x%04X", cpu.getAccD()));
        xLabel.setText(String.format("0x%04X", cpu.getRegX()));
        yLabel.setText(String.format("0x%04X", cpu.getRegY()));
        uLabel.setText(String.format("0x%04X", cpu.getRegU()));
        sLabel.setText(String.format("0x%04X", cpu.getRegS()));
        pcLabel.setText(String.format("0x%04X", cpu.getRegPC()));
        dpLabel.setText(String.format("0x%02X", cpu.getRegDP()));
        ccLabel.setText(String.format("0x%02X", cpu.getRegCC()));
    }
}