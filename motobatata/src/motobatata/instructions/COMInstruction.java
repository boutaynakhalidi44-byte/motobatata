package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class COMInstruction implements Instruction {

    public static final int DIRECT = 1;
    public static final int EXTENDED = 2;
    public static final int INDEXED_X = 3;

    private final String mnemonic;
    private final int mode;

    public COMInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
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

        int value = memory.readByte(addr);
        int result = (~value) & 0xFF;
        memory.writeByte(addr, result);

        cpu.setFlag(CPU.CC_N, (result & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, result == 0);
        cpu.setFlag(CPU.CC_V, false);
        cpu.setFlag(CPU.CC_C, true);
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
