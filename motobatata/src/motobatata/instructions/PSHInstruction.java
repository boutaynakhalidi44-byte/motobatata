package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class PSHInstruction implements Instruction {

    private final String mnemonic;
    private final String registerName;

    public PSHInstruction(String mnemonic, String registerName) {
        this.mnemonic = mnemonic;
        this.registerName = registerName;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        switch (registerName.toUpperCase()) {
            case "A":
                cpu.pushByte(cpu.getAccA());
                break;
            case "B":
                cpu.pushByte(cpu.getAccB());
                break;
            case "CC":
                cpu.pushByte(cpu.getRegCC());
                break;
            case "DP":
                cpu.pushByte(cpu.getRegDP());
                break;
            case "X":
                cpu.pushWord(cpu.getRegX());
                break;
            case "Y":
                cpu.pushWord(cpu.getRegY());
                break;
            case "U":
                cpu.pushWord(cpu.getRegU());
                break;
            case "S":
                cpu.pushWord(cpu.getRegS());
                break;
            default:
                throw new IllegalArgumentException("Registre inconnu pour PSH");
        }
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public int getSize() {
        return 1; // inherent
    }
}
