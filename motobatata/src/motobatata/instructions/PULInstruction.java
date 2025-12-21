package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class PULInstruction implements Instruction {

    private final String mnemonic;
    private final String registerName;

    public PULInstruction(String mnemonic, String registerName) {
        this.mnemonic = mnemonic;
        this.registerName = registerName;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        switch (registerName.toUpperCase()) {
            case "A":
                cpu.setAccA(cpu.popByte());
                break;
            case "B":
                cpu.setAccB(cpu.popByte());
                break;
            case "CC":
                cpu.setRegCC(cpu.popByte());
                break;
            case "DP":
                cpu.setRegDP(cpu.popByte());
                break;
            case "X":
                cpu.setRegX(cpu.popWord());
                break;
            case "Y":
                cpu.setRegY(cpu.popWord());
                break;
            case "U":
                cpu.setRegU(cpu.popWord());
                break;
            case "S":
                cpu.setRegS(cpu.popWord());
                break;
            default:
                throw new IllegalArgumentException("Registre inconnu pour PUL");
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
