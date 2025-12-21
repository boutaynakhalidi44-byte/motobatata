package motobatata.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class SimulatorFrame extends JFrame {

    public SimulatorFrame(CPU cpu) {

        setTitle("Motorola 6809 - Simulator | Émulateur du Microprocesseur 6809");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(createIcon());

        Color bg = new Color(210, 170, 180);
        getContentPane().setBackground(bg);
        setLayout(new BorderLayout(10, 10));

        // Create panels
        RegisterPanel registerPanel = new RegisterPanel(cpu);
        CodeEditorPanel codeEditor = new CodeEditorPanel();
        MemoryPanel memoryPanel = new MemoryPanel(cpu.getMemory());
        FlagsPanel flagsPanel = new FlagsPanel(cpu);
        UALPanel ualPanel = new UALPanel();
        ControlPanel controlPanel = new ControlPanel(cpu, registerPanel, memoryPanel, codeEditor);
        controlPanel.setFlagsPanel(flagsPanel);

        // Left: Registers + Flags
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(bg);
        leftPanel.add(registerPanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(flagsPanel);
        leftPanel.add(Box.createVerticalGlue());
        
        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setPreferredSize(new Dimension(250, 600));

        // Center: Code Editor + Memory (split pane)
        JSplitPane centerSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                codeEditor,
                memoryPanel
        );
        centerSplit.setResizeWeight(0.4);
        centerSplit.setDividerLocation(0.4);

        // Right: UAL + Info
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(bg);
        rightPanel.add(ualPanel);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.setPreferredSize(new Dimension(250, 600));

        // Bottom: Control panel
        controlPanel.setPreferredSize(new Dimension(1400, 150));

        // Main layout
        add(leftScroll, BorderLayout.WEST);
        add(centerSplit, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        // Menu bar
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        JMenuItem guideItem = new JMenuItem("Quick Guide");
        guideItem.addActionListener(e -> showGuide());
        helpMenu.add(aboutItem);
        helpMenu.add(new JSeparator());
        helpMenu.add(guideItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "Motorola 6809 Simulator v1.0\n" +
            "Educational emulator for the 6809 microprocessor\n\n" +
            "© 2025 - Academic Project",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGuide() {
        JOptionPane.showMessageDialog(this,
            "GUIDE D'UTILISATION:\n\n" +
            "1. Écrivez du code assembleur dans l'éditeur en haut\n" +
            "2. Cliquez sur 'COMPILER' pour assembler le code\n" +
            "3. Cliquez 'STEP' pour exécuter une instruction\n" +
            "4. Cliquez 'RUN' pour exécuter en continu\n" +
            "5. Cliquez 'PAUSE' pour arrêter l'exécution\n" +
            "6. Les registres se mettent à jour en temps réel\n" +
            "7. Vous pouvez ajouter des points d'arrêt\n\n" +
            "Instructions supportées: LDA, LDB, LDD, STA, STB, STD,\n" +
            "ADDA, ADDB, ADDD, SUBA, SUBB, SUBD, CMP, CMPD,\n" +
            "BRA, BEQ, BNE, LBRA, LBSR, JSR, RTS, et bien d'autres...\n\n" +
            "Exemple de code:\n" +
            "  LDA #$10\n" +
            "  ADDA #$05\n" +
            "  STA $20",
            "Aide", JOptionPane.INFORMATION_MESSAGE);
    }

    private Image createIcon() {
        // Create a simple 16x16 icon
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(210, 170, 180));
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(2, 2, 12, 12);
        g2d.dispose();
        return img;
    }

    public static void main(String[] args) {
        Memory memory = new Memory();
        CPU cpu = new CPU(memory);

        SwingUtilities.invokeLater(() -> {
            SimulatorFrame frame = new SimulatorFrame(cpu);
            frame.setVisible(true);
        });
    }
}