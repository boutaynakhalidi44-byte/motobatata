package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * Instruction DAA - Decimal Adjust Accumulator A
 */
public class DAAInstruction implements Instruction {

    @Override
    public void execute(CPU cpu, Memory memory) {

        int a = cpu.getAccA();
        int correction = 0;

        boolean halfCarry = cpu.isFlagSet(CPU.CC_H);
        boolean carry = cpu.isFlagSet(CPU.CC_C);

        // Ajustement du demi-octet bas
        if (halfCarry || (a & 0x0F) > 0x09) {
            correction += 0x06;
        }

        // Ajustement de l'octet haut
        if (carry || a > 0x99) {
            correction += 0x60;
            cpu.setFlag(CPU.CC_C, true);
        }

        int result = (a + correction) & 0xFF;
        cpu.setAccA(result);

        // Mise à jour des flags
        cpu.setFlag(CPU.CC_N, (result & 0x80) != 0);
        cpu.setFlag(CPU.CC_Z, result == 0);
        cpu.setFlag(CPU.CC_V, false); // indéfini sur 6809
    }

    @Override
    public String getMnemonic() {
        return "DAA";
    }

    @Override
    public int getSize() {
        return 1; // instruction inhérente
    }
}
