package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class LSRInstruction implements Instruction {

    private final String mnemonic;
    private final boolean useA;

    public LSRInstruction(String mnemonic, boolean useA) {
        this.mnemonic = mnemonic;
        this.useA = useA;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {

        int value = useA ? cpu.getAccA() : cpu.getAccB();

        cpu.setFlag(CPU.CC_C, (value & 0x01) != 0);

        int result = (value >> 1) & 0x7F;

        if (useA) cpu.setAccA(result);
        else cpu.setAccB(result);

        cpu.setFlag(CPU.CC_N, false);
        cpu.setFlag(CPU.CC_Z, result == 0);
        cpu.setFlag(CPU.CC_V, false);
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
