package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class BGTInstruction implements Instruction {
    private final String mnemonic = "BGT";
    @Override
    public void execute(CPU cpu, Memory memory) {
        int target = AddressingMode.relative8(cpu);
        if (!cpu.isFlagSet(CPU.CC_Z) && cpu.isFlagSet(CPU.CC_N) == cpu.isFlagSet(CPU.CC_V))
            cpu.setRegPC(target);
    }
    @Override public String getMnemonic() { return mnemonic; }
    @Override public int getSize() { return 2; }
}
