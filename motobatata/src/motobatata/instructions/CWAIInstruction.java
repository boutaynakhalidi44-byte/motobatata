package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class CWAIInstruction implements Instruction {
    private final String mnemonic = "CWAI";

    @Override
    public void execute(CPU cpu, Memory memory) {
        // Empiler CC et attendre interruption
        cpu.pushByte(cpu.getRegCC());
        cpu.setWaiting(true);
    }

    @Override
    public String getMnemonic() { return mnemonic; }

    @Override
    public int getSize() { return 2; } // opcode + mask
}
