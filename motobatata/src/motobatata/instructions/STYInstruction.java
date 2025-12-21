package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * STY - Store Y register (16-bit)
 * Stores a 16-bit value from Y into memory
 */
public class STYInstruction implements Instruction {

    public static final int DIRECT = 0;
    public static final int EXTENDED = 1;
    public static final int INDEXED_X = 2;

    private final String mnemonic;
    private final int mode;

    public STYInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int y = cpu.getRegY();
        int address;

        switch (mode) {
            case DIRECT:
                address = AddressingMode.direct(cpu);
                break;
            case EXTENDED:
                address = AddressingMode.extended(cpu);
                break;
            case INDEXED_X:
                address = AddressingMode.indexedX(cpu);
                break;
            default:
                throw new IllegalStateException("Mode invalide");
        }

        memory.writeWord(address, y);

        cpu.setFlag(CPU.CC_N, (y & 0x8000) != 0);
        cpu.setFlag(CPU.CC_Z, y == 0);
        cpu.setFlag(CPU.CC_V, false);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        switch(mode) {
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
