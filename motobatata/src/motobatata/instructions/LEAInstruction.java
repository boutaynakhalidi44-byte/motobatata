package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class LEAInstruction implements Instruction {
    
    public static final int INDEXED_X = 3;
    public static final int EXTENDED = 2;
    
    private final String mnemonic = "LEA";
    private final String reg; // X, Y, U, S
    private final int mode;

    public LEAInstruction(String reg, int mode) {
        this.reg = reg;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int addr;
        switch (mode) {
            case INDEXED_X:
                addr = AddressingMode.indexedX(cpu);
                break;
            case EXTENDED:
                addr = AddressingMode.extended(cpu);
                break;
            default:
                addr = 0;
        }

        switch (reg) {
            case "X":
                cpu.setRegX(addr);
                break;
            case "Y":
                cpu.setRegY(addr);
                break;
            case "U":
                cpu.setRegU(addr);
                break;
            case "S":
                cpu.setRegS(addr);
                break;
        }
    }

    @Override
    public String getMnemonic() { 
        return mnemonic; 
    }

    @Override
    public int getSize() { 
        return (mode == EXTENDED) ? 3 : 2; 
    }
}
