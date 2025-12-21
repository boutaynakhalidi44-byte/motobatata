package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class STInstruction implements Instruction {

    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final boolean useA;
    private final int mode;

    public STInstruction(String mnemonic, boolean useA, int mode) {
        this.mnemonic = mnemonic;
        this.useA = useA;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int addr;

        switch (mode) {
            case DIRECT:
                addr = AddressingMode.direct(cpu);
                break;
            case EXTENDED:
                addr = AddressingMode.extended(cpu);
                break;
            case INDEXED_X:
                addr = AddressingMode.indexedX(cpu);
                break;
            default:
                throw new IllegalStateException();
        }

        int value = useA ? cpu.getAccA() : cpu.getAccB();
        value &= 0xFF;

        memory.writeByte(addr, value);

        cpu.setFlag(CPU.CC_N, (value & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, value == 0);
        cpu.setFlag(CPU.CC_V, false);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
