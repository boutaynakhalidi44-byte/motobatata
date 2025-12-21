package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

/**
 * LBRA - Long Branch Always (16-bit offset)
 */
public class LBRAInstruction implements Instruction {

    private final String mnemonic;

    public LBRAInstruction(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int offset = AddressingMode.immediate16(cpu);
        int newPC = (cpu.getRegPC() + offset) & 0xFFFF;
        cpu.setRegPC(newPC);
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 3; // 1 opcode + 2 offset
    }
}
