package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

/**
 * STD - Store D (A:B accumulator pair) into memory at 16-bit address
 */
public class STDInstruction implements Instruction {

    public static final int DIRECT = 0;
    public static final int EXTENDED = 1;
    public static final int INDEXED_X = 2;

    private final String mnemonic;
    private final int mode;

    public STDInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int d = cpu.getAccD();
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

        memory.writeWord(address, d);

        cpu.setFlag(CPU.CC_N, (d & 0x8000) != 0);
        cpu.setFlag(CPU.CC_Z, d == 0);
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
                return 1;
        }
    }
}
