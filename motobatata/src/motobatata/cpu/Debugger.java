package motobatata.cpu;

import java.util.HashSet;
import java.util.Set;


public class Debugger {

    private final CPU cpu;
    private final Set<Integer> breakpoints;

    private boolean paused;
    private boolean stepMode;
    private int lastExecutedPC;

    // ===== CONSTRUCTEUR ====
    

    public Debugger(CPU cpu) {
        this.cpu = cpu;
        this.breakpoints = new HashSet<>();
        this.paused = false;
        this.stepMode = false;
        this.lastExecutedPC = -1;
    }


    // ===== BREAKPOINTS =====
    

    public void addBreakpoint(int address) {
        breakpoints.add(address & 0xFFFF);
    }

    public void removeBreakpoint(int address) {
        breakpoints.remove(address & 0xFFFF);
    }

    public boolean isBreakpoint(int address) {
        return breakpoints.contains(address & 0xFFFF);
    }

    public Set<Integer> getBreakpoints() {
        return new HashSet<>(breakpoints);
    }

    public void clearBreakpoints() {
        breakpoints.clear();
    }

  
    // ===== PAUSE ===========
   

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

  
    // ===== STEP MODE =======
   

    public void setStepMode(boolean stepMode) {
        this.stepMode = stepMode;
        this.paused = stepMode; // cohérence logique
    }

    public boolean isStepMode() {
        return stepMode;
    }

   
    // ===== STEP ============
    

    /**
     * Exécute UNE instruction (mode pas à pas)
     */
    public void step() {
        if (cpu.isHalted()) return;
        if (!stepMode) return;

        lastExecutedPC = cpu.getRegPC();
        cpu.executeInstruction();
        paused = true;
    }

   
    // ===== CONTINUE ========
   

    /**
     * Continue l'exécution normale
     */
    public void continueExecution() {
        paused = false;
        stepMode = false;
    }

    // ===== RUN ============
  

    /**
     * Exécution continue jusqu'à breakpoint ou arrêt
     */
    public void run() {
        paused = false;
        stepMode = false;

        while (!paused && !cpu.isHalted()) {

            // Arrêt AVANT exécution si breakpoint atteint
            if (isBreakpoint(cpu.getRegPC())) {
                paused = true;
                break;
            }

            lastExecutedPC = cpu.getRegPC();
            cpu.executeInstruction();
        }
    }

    // ===== INFOS ===========
    

    public int getLastExecutedPC() {
        return lastExecutedPC;
    }

    /**
     * État du CPU pour affichage GUI
     */
    public String getCPUState() {
        return String.format(
            "PC:%04X  A:%02X  B:%02X  D:%04X  X:%04X  Y:%04X  U:%04X  S:%04X  DP:%02X  CC:%02X",
            cpu.getRegPC(),
            cpu.getAccA(),
            cpu.getAccB(),
            cpu.getAccD(),
            cpu.getRegX(),
            cpu.getRegY(),
            cpu.getRegU(),
            cpu.getRegS(),
            cpu.getRegDP(),
            cpu.getRegCC()
        );
    }
}

