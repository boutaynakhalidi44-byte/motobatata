package motobatata.instructions;

import motobatata.addressing.AddressingMode;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class JMPInstruction implements Instruction {

    public static final int INDEXED_X = 3;
    public static final int EXTENDED = 2;

    private final String mnemonic;
    private final int mode;

    public JMPInstruction(String mnemonic, int mode) {
        this.mnemonic = mnemonic;
        this.mode = mode;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int target;
        switch (mode) {
            case EXTENDED:
                target = AddressingMode.extended(cpu);
                break;
            case INDEXED_X:
                target = AddressingMode.indexedX(cpu);
                break;
            default:
                target = cpu.getRegPC();
        }
        cpu.setRegPC(target);
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
