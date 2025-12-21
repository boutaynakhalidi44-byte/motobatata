package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * Instruction SBC - SBCA / SBCB
 */
public class SBCInstruction implements Instruction {

    public static final int IMM8 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;
    public static final int INDEXED_Y = 4;
    public static final int INDEXED_U = 5;
    public static final int INDEXED_S = 6;
    public static final int INDEXED_PC = 7;

    private final String mnemonic;
    private final boolean useA;
    private final int addressingType;

    public SBCInstruction(String mnemonic, boolean useA, int addressingType) {
        this.mnemonic = mnemonic;
        this.useA = useA;
        this.addressingType = addressingType;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int acc = useA ? cpu.getAccA() : cpu.getAccB();
        int borrow = cpu.isFlagSet(CPU.CC_C) ? 1 : 0;
        int operand;

        switch (addressingType) {
            case IMM8: operand = AddressingMode.immediate8(cpu); break;
            case DIRECT: operand = memory.readByte(AddressingMode.direct(cpu)); break;
            case EXTENDED: operand = memory.readByte(AddressingMode.extended(cpu)); break;
            case INDEXED_X: operand = memory.readByte(AddressingMode.indexedX(cpu)); break;
            case INDEXED_Y: operand = memory.readByte(AddressingMode.indexedY(cpu)); break;
            case INDEXED_U: operand = memory.readByte(AddressingMode.indexedU(cpu)); break;
            case INDEXED_S: operand = memory.readByte(AddressingMode.indexedS(cpu)); break;
            case INDEXED_PC: operand = memory.readByte(AddressingMode.indexedPC(cpu)); break;
            default: throw new IllegalStateException("Mode invalide");
        }

        int result = acc - operand - borrow;
        int finalValue = result & 0xFF;

        if (useA) cpu.setAccA(finalValue);
        else cpu.setAccB(finalValue);

        updateFlags(cpu, acc, operand, borrow, result);
    }

    private void updateFlags(CPU cpu, int a, int b, int borrow, int result) {
        cpu.setFlag(CPU.CC_N, (result & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, (result & 0xFF) == 0);
        cpu.setFlag(
            CPU.CC_V,
            ((a ^ b) & (a ^ result) & 0x80) != 0
        );
        cpu.setFlag(CPU.CC_C, result < 0);
        cpu.setFlag(
            CPU.CC_H,
            ((a & 0x0F) - (b & 0x0F) - borrow) < 0
        );
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
