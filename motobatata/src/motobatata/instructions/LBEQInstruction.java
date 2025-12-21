package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

/**
 * LBEQ - Long Branch if Equal (16-bit offset, Z flag = 1)
 */
public class LBEQInstruction implements Instruction {

    private final String mnemonic;

    public LBEQInstruction(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int offset = AddressingMode.immediate16(cpu);
        
        if (cpu.isFlagSet(CPU.CC_Z)) {
            int newPC = (cpu.getRegPC() + offset) & 0xFFFF;
            cpu.setRegPC(newPC);
        }
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 3;
    }
}
