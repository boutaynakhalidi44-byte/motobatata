package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * Instruction MUL - Multiply A by B
 */
public class MULInstruction implements Instruction {

    @Override
    public void execute(CPU cpu, Memory memory) {

        int a = cpu.getAccA();
        int b = cpu.getAccB();

        int result = a * b; // 16 bits

        cpu.setAccA((result >> 8) & 0xFF);
        cpu.setAccB(result & 0xFF);

        // Flags
        cpu.setFlag(CPU.CC_Z, result == 0);
        cpu.setFlag(CPU.CC_C, (result & 0x8000) != 0);
    }

    @Override
    public String getMnemonic() {
        return "MUL";
    }

    @Override
    public int getSize() {
        return 1;
    }
}
