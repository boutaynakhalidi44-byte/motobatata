package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

/**
 * LBSR - Long Branch to Subroutine (16-bit offset)
 */
public class LBSRInstruction implements Instruction {

    private final String mnemonic;

    public LBSRInstruction(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        // Push return address on stack
        int returnAddr = cpu.getRegPC();
        cpu.pushWord(returnAddr);
        
        // Branch to subroutine
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
