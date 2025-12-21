package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class BPLInstruction implements Instruction {
    private final String mnemonic = "BPL";
    @Override
    public void execute(CPU cpu, Memory memory) {
        int target = AddressingMode.relative8(cpu);
        if (!cpu.isFlagSet(CPU.CC_N)) cpu.setRegPC(target);
    }
    @Override public String getMnemonic() { return mnemonic; }
    @Override public int getSize() { return 2; }
}
