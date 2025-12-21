package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class AddInstruction implements Instruction {

    private final String mnemonic;
    private final boolean useA; // true = A, false = B
    private final int addressingType;

    public static final int IMM8 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;
    public static final int INDEXED_Y = 4;
    public static final int INDEXED_U = 5;
    public static final int INDEXED_S = 6;
    public static final int INDEXED_PC = 7;

    public AddInstruction(String mnemonic, boolean useA, int addressingType) {
        this.mnemonic = mnemonic;
        this.useA = useA;
        this.addressingType = addressingType;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int acc = useA ? cpu.getAccA() : cpu.getAccB();
        int operand;

        switch (addressingType) {

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

            case INDEXED_Y:
                operand = memory.readByte(AddressingMode.indexedY(cpu));
                break;

            case INDEXED_U:
                operand = memory.readByte(AddressingMode.indexedU(cpu));
                break;

            case INDEXED_S:
                operand = memory.readByte(AddressingMode.indexedS(cpu));
                break;

            case INDEXED_PC:
                operand = memory.readByte(AddressingMode.indexedPC(cpu));
                break;

            default:
                throw new IllegalStateException("Mode d'adressage invalide");
        }

        int result = acc + operand;
        int finalValue = result & 0xFF;

        if (useA) {
            cpu.setAccA(finalValue);
        } else {
            cpu.setAccB(finalValue);
        }

        updateFlags(cpu, acc, operand, result);
    }

    private void updateFlags(CPU cpu, int a, int b, int result) {
        int finalValue = result & 0xFF;
        cpu.setFlag(CPU.CC_N, (finalValue & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, finalValue == 0);
        cpu.setFlag(CPU.CC_V, ((a ^ result) & (b ^ result) & 0x80) != 0);
        cpu.setFlag(CPU.CC_C, (result & 0x100) != 0);
        cpu.setFlag(CPU.CC_H, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 1; // le PC avance via fetchByte/fetchWord()
    }
}
