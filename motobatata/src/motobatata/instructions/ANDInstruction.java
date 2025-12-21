package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class ANDInstruction implements Instruction {

    public static final int IMM8 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final boolean useA;
    private final int mode;

    public ANDInstruction(String mnemonic, boolean useA, int mode) {
        this.mnemonic = mnemonic;
        this.useA = useA;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int operand;
        switch (mode) {
            case IMM8:
                operand = AddressingMode.immediate8(cpu);
                break;
            case DIRECT:
                operand = memory.readByte(AddressingMode.direct(cpu));
                break;
            case EXTENDED:
                operand = memory.readByte(AddressingMode.extended(cpu));
                break;
            case INDEXED_X:
                operand = memory.readByte(AddressingMode.indexedX(cpu));
                break;
            default:
                throw new IllegalStateException();
        }

        int acc = useA ? cpu.getAccA() : cpu.getAccB();
        int result = (acc & operand) & 0xFF;

        if (useA) cpu.setAccA(result);
        else cpu.setAccB(result);

        cpu.setFlag(CPU.CC_N, (result & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, result == 0);
        cpu.setFlag(CPU.CC_V, false);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 0; // PC géré par fetch
    }
}

