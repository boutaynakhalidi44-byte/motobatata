package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class SYNCInstruction implements Instruction {
    private final String mnemonic = "SYNC";

    @Override
    public void execute(CPU cpu, Memory memory) {
        cpu.setWaiting(true); // Attend interruption
    }

    @Override
    public String getMnemonic() { return mnemonic; }

    @Override
    public int getSize() { return 1; }
}
