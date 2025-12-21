package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class LDInstruction implements Instruction {

    public static final int IMM8 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final boolean useA;
    private final int mode;

    public LDInstruction(String mnemonic, boolean useA, int mode) {
        this.mnemonic = mnemonic;
        this.useA = useA;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int value;

        switch (mode) {
            case IMM8:
                value = AddressingMode.immediate8(cpu);
                break;
            case DIRECT:
                value = memory.readByte(AddressingMode.direct(cpu));
                break;
            case EXTENDED:
                value = memory.readByte(AddressingMode.extended(cpu));
                break;
            case INDEXED_X:
                value = memory.readByte(AddressingMode.indexedX(cpu));
                break;
            default:
                throw new IllegalStateException();
        }

        value &= 0xFF;

        if (useA) cpu.setAccA(value);
        else cpu.setAccB(value);

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
        switch(mode) {
            case IMM8:
                return 2;      // 1 opcode + 1 operand
            case DIRECT:
                return 2;    // 1 opcode + 1 address
            case EXTENDED:
                return 3;  // 1 opcode + 2 address
            case INDEXED_X:
                return 2; // 1 opcode + 1 postbyte
            default:
                return 1;
        }
    }
}
