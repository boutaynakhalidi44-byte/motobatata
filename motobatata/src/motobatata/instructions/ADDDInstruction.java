package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

/**
 * ADDD - Add 16-bit value to D (A:B accumulator pair)
 * D = D + operand
 */
public class ADDDInstruction implements Instruction {

    public static final int IMM16 = 0;
    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final int mode;

    public ADDDInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int d = cpu.getAccD();
        int operand;

        switch (mode) {
            case IMM16:
                operand = AddressingMode.immediate16(cpu);
                break;
            case DIRECT:
                int dirAddr = AddressingMode.direct(cpu);
                operand = memory.readWord(dirAddr);
                break;
            case EXTENDED:
                int extAddr = AddressingMode.extended(cpu);
                operand = memory.readWord(extAddr);
                break;
            case INDEXED_X:
                int idxAddr = AddressingMode.indexedX(cpu);
                operand = memory.readWord(idxAddr);
                break;
            default:
                throw new IllegalStateException("Mode invalide");
        }

        int result = d + operand;
        int finalValue = result & 0xFFFF;

        cpu.setAccD(finalValue);

        // Update flags for 16-bit operation
        cpu.setFlag(CPU.CC_N, (finalValue & 0x8000) != 0);
        cpu.setFlag(CPU.CC_Z, finalValue == 0);
        cpu.setFlag(CPU.CC_V, ((d ^ operand) & (d ^ result) & 0x8000) != 0);
        cpu.setFlag(CPU.CC_C, (result & 0x10000) != 0);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        switch(mode) {
            case IMM16:
                return 3;     // 1 opcode + 2 operand
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
