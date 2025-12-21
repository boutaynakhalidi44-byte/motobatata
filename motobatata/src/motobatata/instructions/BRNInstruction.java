package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;
import motobatata.addressing.AddressingMode;

public class BRNInstruction implements Instruction {
    private final String mnemonic = "BRN";
    @Override
    public void execute(CPU cpu, Memory memory) {
        AddressingMode.relative8(cpu); // consomme l'octet de déplacement mais ne branche jamais
    }
    @Override public String getMnemonic() { return mnemonic; }
    @Override public int getSize() { return 2; }
}
