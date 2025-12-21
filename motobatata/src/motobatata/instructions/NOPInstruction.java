package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class NOPInstruction implements Instruction {

    @Override
    public void execute(CPU cpu, Memory memory) {
        // ne fait rien
    }

    @Override
    public String getMnemonic() {
        return "NOP";
    }

    @Override
    public int getSize() {
        return 1;
    }
}
