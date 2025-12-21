package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class BSRInstruction implements Instruction {
    private final String mnemonic = "BSR";
    @Override
    public void execute(CPU cpu, Memory memory) {
        int target = AddressingMode.relative8(cpu);
        // Empiler l'adresse de retour (PC après l'instruction)
        int returnAddr = cpu.getRegPC();
        cpu.pushWord(returnAddr);
        cpu.setRegPC(target);
    }
    @Override public String getMnemonic() { return mnemonic; }
    @Override public int getSize() { return 2; }
}
