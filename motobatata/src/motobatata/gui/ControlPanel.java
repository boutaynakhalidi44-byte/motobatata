package motobatata.gui;

import java.awt.*;
import javax.swing.*;
import motobatata.assembler.Assembler;
import motobatata.cpu.CPU;
import motobatata.cpu.Debugger;

public class ControlPanel extends JPanel {

    private final CPU cpu;
    private final RegisterPanel registerPanel;
    private final MemoryPanel memoryPanel;
    private final CodeEditorPanel codeEditorPanel;
    private FlagsPanel flagsPanel;
    private final Debugger debugger;
    private final Assembler assembler;
    
    private JButton stepBtn, runBtn, pauseBtn, resetBtn;
    private JButton compileBtn, addBreakpointBtn, clearBreakpointsBtn;
    private JLabel statusLabel;
    private boolean running = false;
    private boolean compiled = false;

    public ControlPanel(CPU cpu,
                        RegisterPanel rp,
                        MemoryPanel mp,
                        CodeEditorPanel codeEditor) {
        this.cpu = cpu;
        this.registerPanel = rp;
        this.memoryPanel = mp;
        this.codeEditorPanel = codeEditor;
        this.flagsPanel = null; // Will be set later if needed
        this.debugger = cpu.getDebugger();
        this.assembler = new Assembler();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(210, 170, 180));
        setBorder(BorderFactory.createTitledBorder("Contrôle"));

        // Panel 1: Compilation button
        JPanel compilationPanel = new JPanel();
        compilationPanel.setBackground(new Color(210, 170, 180));
        
        compileBtn = new JButton("COMPILER");
        compileBtn.setFont(new Font("Arial", Font.BOLD, 12));
        compileBtn.setForeground(new Color(0, 100, 0));
        compileBtn.addActionListener(e -> handleCompile());
        
        compilationPanel.add(new JLabel("1. Modifier code ci-dessus, puis :"));
        compilationPanel.add(compileBtn);

        // Panel 2: Execution buttons
        JPanel executionPanel = new JPanel();
        executionPanel.setBackground(new Color(210, 170, 180));
        
        stepBtn = new JButton("STEP");
        runBtn = new JButton("RUN");
        pauseBtn = new JButton("PAUSE");
        resetBtn = new JButton("RESET");
        
        stepBtn.addActionListener(e -> handleStep());
        runBtn.addActionListener(e -> handleRun());
        pauseBtn.addActionListener(e -> handlePause());
        resetBtn.addActionListener(e -> handleReset());
        
        executionPanel.add(new JLabel("2. Exécution :"));
        executionPanel.add(stepBtn);
        executionPanel.add(runBtn);
        executionPanel.add(pauseBtn);
        executionPanel.add(resetBtn);

        // Panel 3: Breakpoint buttons
        JPanel breakpointPanel = new JPanel();
        breakpointPanel.setBackground(new Color(210, 170, 180));
        
        addBreakpointBtn = new JButton("Add Breakpoint");
        clearBreakpointsBtn = new JButton("Clear Breakpoints");
        
        addBreakpointBtn.addActionListener(e -> handleAddBreakpoint());
        clearBreakpointsBtn.addActionListener(e -> {
            debugger.clearBreakpoints();
            statusLabel.setText("Status: Breakpoints cleared");
        });
        
        breakpointPanel.add(addBreakpointBtn);
        breakpointPanel.add(clearBreakpointsBtn);

        // Status label
        statusLabel = new JLabel("Status: Ready - Compilez d'abord");
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        statusLabel.setForeground(Color.DARK_GRAY);

        add(compilationPanel);
        add(executionPanel);
        add(breakpointPanel);
        add(Box.createVerticalStrut(10));
        add(statusLabel);
        add(Box.createVerticalGlue());
    }

    private void handleCompile() {
        try {
            String[] lines = codeEditorPanel.getCodeLines();
            
            if (lines.length == 0 || (lines.length == 1 && lines[0].trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez écrire du code assembleur à compiler", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            byte[] bytecode = assembler.assemble(lines);
            
            if (bytecode.length == 0) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur d'assemblage : aucune instruction valide", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                codeEditorPanel.setStatus("Erreur : Code invalide");
                return;
            }
            
            // Load bytecode into memory
            cpu.reset();
            for (int i = 0; i < bytecode.length; i++) {
                cpu.getMemory().writeByte(i, bytecode[i]);
            }
            
            compiled = true;
            refresh();
            codeEditorPanel.setStatus("✓ Compilation réussie - " + bytecode.length + " octets chargés");
            statusLabel.setText("Status: Code compilé et chargé - Prêt pour exécution");
            
            JOptionPane.showMessageDialog(this, 
                "Compilation réussie!\n" + bytecode.length + " octets chargés en mémoire", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de compilation:\n" + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            codeEditorPanel.setStatus("Erreur : " + e.getMessage());
            compiled = false;
        }
    }

    private void handleStep() {
        if (!compiled) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez compiler le code d'abord (bouton COMPILER)", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cpu.isHalted()) {
            cpu.executeInstruction();  // Exécute directement une instruction
            refresh();
            repaint();
            statusLabel.setText("Status: STEP - PC=" + String.format("0x%04X", cpu.getRegPC()));
        } else {
            statusLabel.setText("Status: CPU arrêtée");
        }
    }

    private void handleRun() {
        if (!compiled) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez compiler le code d'abord (bouton COMPILER)", 
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!running) {
            running = true;
            runBtn.setEnabled(false);
            stepBtn.setEnabled(false);
            compileBtn.setEnabled(false);
            statusLabel.setText("Status: Exécution en cours...");

            Thread runThread = new Thread(() -> {
                while (running && !cpu.isHalted()) {
                    // Execute the instruction directly
                    cpu.executeInstruction();
                    
                    // Refresh GUI on EDT (Event Dispatch Thread)
                    SwingUtilities.invokeLater(() -> {
                        refresh();
                        repaint();
                    });
                    
                    // Check if breakpoint hit
                    if (debugger.getBreakpoints().contains(cpu.getRegPC())) {
                        running = false;
                        break;
                    }
                    
                    try {
                        Thread.sleep(100); // Délai pour visualiser
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Final refresh when done
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    runBtn.setEnabled(true);
                    stepBtn.setEnabled(true);
                    compileBtn.setEnabled(true);
                    refresh();
                    repaint();
                    statusLabel.setText("Status: Arrêté à PC=" + 
                        String.format("0x%04X", cpu.getRegPC()));
                });
            });
            runThread.setDaemon(true);
            runThread.start();
        }
    }

    private void handlePause() {
        running = false;
        runBtn.setEnabled(true);
        stepBtn.setEnabled(true);
        compileBtn.setEnabled(true);
        refresh();
        statusLabel.setText("Status: Paused at PC=" + String.format("0x%04X", cpu.getRegPC()));
    }

    private void handleReset() {
        running = false;
        compiled = false;
        cpu.reset();
        debugger.clearBreakpoints();
        runBtn.setEnabled(true);
        stepBtn.setEnabled(true);
        compileBtn.setEnabled(true);
        refresh();
        statusLabel.setText("Status: CPU Reset - Compilez le code à nouveau");
    }

    private void handleAddBreakpoint() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter breakpoint address (hex):", "0000");
        if (input != null && !input.isEmpty()) {
            try {
                int addr = Integer.parseInt(input, 16) & 0xFFFF;
                debugger.addBreakpoint(addr);
                statusLabel.setText("Status: Breakpoint added at 0x" + 
                    String.format("%04X", addr));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid hex address", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refresh() {
        registerPanel.refresh();
        memoryPanel.refresh();
        if (flagsPanel != null) {
            flagsPanel.refresh();
        }
    }

    public void setFlagsPanel(FlagsPanel fp) {
        this.flagsPanel = fp;
        if (flagsPanel != null) {
            flagsPanel.refresh();
        }
    }
}