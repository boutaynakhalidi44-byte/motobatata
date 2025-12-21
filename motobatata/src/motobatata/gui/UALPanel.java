package motobatata.gui;

import javax.swing.*;
import java.awt.*;

public class UALPanel extends JPanel {

    public UALPanel() {
        setPreferredSize(new Dimension(250,300));
        setBorder(BorderFactory.createTitledBorder("UAL"));
        setBackground(new Color(190,150,160));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawRect(60, 80, 120, 80);
        g.drawString("UAL", 110, 120);

        g.drawLine(20,100,60,100);
        g.drawLine(20,140,60,140);
        g.drawLine(180,120,220,120);
    }
}