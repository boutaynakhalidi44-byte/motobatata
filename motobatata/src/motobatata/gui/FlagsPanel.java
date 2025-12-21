package motobatata.gui;

import javax.swing.*;
import java.awt.*;
import motobatata.cpu.CPU;

public class FlagsPanel extends JPanel {

    private final CPU cpu;
    private final JLabel flags;

    public FlagsPanel(CPU cpu) {
        this.cpu = cpu;
        setBackground(Theme.PANEL);
        setBorder(BorderFactory.createTitledBorder("FLAGS CC"));

        flags = new JLabel();
        flags.setFont(Theme.FONT_NORMAL);
        add(flags);

        refresh();
    }

    public void refresh() {
        flags.setText(String.format(
            "E:%d F:%d H:%d I:%d N:%d Z:%d V:%d C:%d",
            cpu.isFlagSet(CPU.CC_E) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_F) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_H) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_I) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_N) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_Z) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_V) ? 1 : 0,
            cpu.isFlagSet(CPU.CC_C) ? 1 : 0
        ));
    }
}