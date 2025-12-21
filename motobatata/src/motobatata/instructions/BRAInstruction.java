package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class BRAInstruction implements Instruction {
    private final String mnemonic = "BRA";
    @Override
    public void execute(CPU cpu, Memory memory) {
        int target = AddressingMode.relative8(cpu);
        cpu.setRegPC(target);
    }
    @Override public String getMnemonic() { return mnemonic; }
    @Override public int getSize() { return 2; }
}

