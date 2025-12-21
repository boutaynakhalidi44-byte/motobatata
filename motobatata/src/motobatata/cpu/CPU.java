package motobatata.cpu;

import motobatata.memory.Memory;

public class CPU {

    // =======================
    // ===== REGISTRES =======
    // =======================

    // Accumulateurs 8 bits
    private int accA;
    private int accB;

    // Accumulateur 16 bits (A:B)
    private int accD;

    // Registres index 16 bits
    private int regX;
    private int regY;
    private int regU;   // User Stack Pointer
    private int regS;   // System Stack Pointer

    // Registres de contrôle
    private int regPC;  // Program Counter
    private int regDP;  // Direct Page Register

    // Condition Code Register
    // Bits: E F H I N Z V C
    private int regCC;

    // =======================
    // ===== ÉTAT CPU ========
    // =======================

    private boolean halted;
    private boolean waiting;

    // =======================
    // ===== COMPOSANTS ======
    // =======================

    private final Memory memory;
    private final Debugger debugger;

    // =======================
    // ===== FLAGS CC ========
    // =======================

    public static final int CC_E = 0x80;
    public static final int CC_F = 0x40;
    public static final int CC_H = 0x20;
    public static final int CC_I = 0x10;
    public static final int CC_N = 0x08;
    public static final int CC_Z = 0x04;
    public static final int CC_V = 0x02;
    public static final int CC_C = 0x01;

    // =======================
    // ===== CONSTRUCTEUR ====
    // =======================

    public CPU(Memory memory) {
        this.memory = memory;
        this.debugger = new Debugger(this);
        reset();
    }

    // =======================
    // ===== RESET CPU =======
    // =======================

    public void reset() {
        accA = accB = 0;
        updateAccD();

        regX = regY = 0;
        regU = regS = 0;
        regDP = 0;

        regCC = CC_I | CC_F; // Interruptions masquées
        halted = false;
        waiting = false;

        // PC initial (simplifié pour projet académique)
        regPC = 0x0000;
    }

    // =======================
    // ===== FETCH ===========
    // =======================

    public int fetchByte() {
        int value = memory.readByte(regPC) & 0xFF;
        regPC = (regPC + 1) & 0xFFFF;
        return value;
    }

    public int fetchWord() {
        int hi = fetchByte();
        int lo = fetchByte();
        return ((hi << 8) | lo) & 0xFFFF;
    }

    // =======================
    // ===== EXÉCUTION =======
    // =======================

    public void executeInstruction() {
        if (halted || waiting) return;

        if (debugger.isBreakpoint(regPC)) {
            debugger.setPaused(true);
            return;
        }

        int opcode = fetchByte();
        motobatata.instructions.Instruction instruction = motobatata.decoder.InstructionDecoder.decode(opcode, this);

        if (instruction != null) {
            instruction.execute(this, memory);
        } else {
            System.err.printf(
                "Opcode inconnu à l'adresse %04X : %02X%n",
                regPC - 1, opcode
            );
            halted = true;
        }
    }

    public void step() {
        executeInstruction();
    }

    // =======================
    // ===== FLAGS ===========
    // =======================

    public boolean isFlagSet(int flag) {
        return (regCC & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        if (value) regCC |= flag;
        else regCC &= ~flag;
    }

    private void updateNZ(int value) {
        setFlag(CC_Z, (value & 0xFF) == 0);
        setFlag(CC_N, (value & 0x80) != 0);
    }

    // =======================
    // ===== GETTERS =========
    // =======================

    public int getAccA() { return accA & 0xFF; }
    public int getAccB() { return accB & 0xFF; }
    public int getAccD() { return accD & 0xFFFF; }

    public int getRegX() { return regX & 0xFFFF; }
    public int getRegY() { return regY & 0xFFFF; }
    public int getRegU() { return regU & 0xFFFF; }
    public int getRegS() { return regS & 0xFFFF; }
    public int getRegPC() { return regPC & 0xFFFF; }
    public int getRegDP() { return regDP & 0xFF; }
    public int getRegCC() { return regCC & 0xFF; }

    // =======================
    // ===== SETTERS =========
    // =======================

    public void setAccA(int value) {
        accA = value & 0xFF;
        updateAccD();
        updateNZ(accA);
    }

    public void setAccB(int value) {
        accB = value & 0xFF;
        updateAccD();
        updateNZ(accB);
    }

    public void setAccD(int value) {
        accD = value & 0xFFFF;
        accA = (accD >> 8) & 0xFF;
        accB = accD & 0xFF;
        updateNZ(accB);
    }

    private void updateAccD() {
        accD = ((accA << 8) | accB) & 0xFFFF;
    }

    public void setRegX(int value) { regX = value & 0xFFFF; }
    public void setRegY(int value) { regY = value & 0xFFFF; }
    public void setRegU(int value) { regU = value & 0xFFFF; }
    public void setRegS(int value) { regS = value & 0xFFFF; }
    public void setRegPC(int value){ regPC = value & 0xFFFF; }
    public void setRegDP(int value){ regDP = value & 0xFF; }
    public void setRegCC(int value){ regCC = value & 0xFF; }

    // =======================
    // ===== PILES ===========
    // =======================

    public void pushByte(int value) {
        regS = (regS - 1) & 0xFFFF;
        memory.writeByte(regS, value & 0xFF);
    }

    public int popByte() {
        int value = memory.readByte(regS) & 0xFF;
        regS = (regS + 1) & 0xFFFF;
        return value;
    }

    public void pushWord(int value) {
        pushByte(value & 0xFF);
        pushByte((value >> 8) & 0xFF);
    }

    public int popWord() {
        int hi = popByte();
        int lo = popByte();
        return ((hi << 8) | lo) & 0xFFFF;
    }

    public void pushByteU(int value) {
        regU = (regU - 1) & 0xFFFF;
        memory.writeByte(regU, value & 0xFF);
    }

    public int popByteU() {
        int value = memory.readByte(regU) & 0xFF;
        regU = (regU + 1) & 0xFFFF;
        return value;
    }

    public void pushWordU(int value) {
        pushByteU(value & 0xFF);
        pushByteU((value >> 8) & 0xFF);
    }

    public int popWordU() {
        int hi = popByteU();
        int lo = popByteU();
        return ((hi << 8) | lo) & 0xFFFF;
    }

    // =======================
    // ===== AUTRES ==========
    // =======================

    public boolean isHalted() { return halted; }
    public void setHalted(boolean halted) { this.halted = halted; }

    public boolean isWaiting() { return waiting; }
    public void setWaiting(boolean waiting) { this.waiting = waiting; }

    public Memory getMemory() { return memory; }
    public Debugger getDebugger() { return debugger; }
}

