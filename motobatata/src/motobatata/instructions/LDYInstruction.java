package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * LDY - Load Y register (16-bit)
 * Loads a 16-bit value into the Y register
 */
public class LDYInstruction implements Instruction {

    public static final int IMM16 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final int mode;

    public LDYInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int value;

        switch (mode) {
            case IMM16:
                value = AddressingMode.immediate16(cpu);
                break;
            case DIRECT:
                int dirAddr = AddressingMode.direct(cpu);
                value = memory.readWord(dirAddr);
                break;
            case EXTENDED:
                int extAddr = AddressingMode.extended(cpu);
                value = memory.readWord(extAddr);
                break;
            case INDEXED_X:
                int idxAddr = AddressingMode.indexedX(cpu);
                value = memory.readWord(idxAddr);
                break;
            default:
                throw new IllegalStateException("Mode invalide");
        }

        // Load Y register
        cpu.setRegY(value & 0xFFFF);

        // Update flags based on loaded value
        cpu.setFlag(CPU.CC_N, (value & 0x8000) != 0);
        cpu.setFlag(CPU.CC_Z, value == 0);
        cpu.setFlag(CPU.CC_V, false); // Always clear overflow
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        switch(mode) {
            case IMM16:
                return 3;
            case DIRECT:
                return 2;
            case EXTENDED:
                return 3;
            case INDEXED_X:
                return 2;
            default:
                return 0;
        }
    }
}
