package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class RTIInstruction implements Instruction {

    private final String mnemonic = "RTI";

    @Override
    public void execute(CPU cpu, Memory memory) {
        // RTI restaure CC et PC depuis la pile (little-endian)
        int cc = cpu.popByte();
        cpu.setRegCC(cc);
        int returnAddr = cpu.popWord();
        cpu.setRegPC(returnAddr);
    }

    @Override
    public String getMnemonic() { return mnemonic; }

    @Override
    public int getSize() { return 1; }
}
