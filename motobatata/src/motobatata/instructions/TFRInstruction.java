package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

public class TFRInstruction implements Instruction {

    private final String mnemonic = "TFR";
    private final String src;
    private final String dest;

    public TFRInstruction(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void execute(CPU cpu, Memory memory) {
        int value = getRegisterValue(cpu, src);
        setRegisterValue(cpu, dest, value);
        // TFR ne modifie pas les flags selon la spécification 6809
    }

    private int getRegisterValue(CPU cpu, String reg) {
        int result;
        switch (reg) {
            case "A":
                result = cpu.getAccA();
                break;
            case "B":
                result = cpu.getAccB();
                break;
            case "D":
                result = cpu.getAccD();
                break;
            case "X":
                result = cpu.getRegX();
                break;
            case "Y":
                result = cpu.getRegY();
                break;
            case "U":
                result = cpu.getRegU();
                break;
            case "S":
                result = cpu.getRegS();
                break;
            case "PC":
                result = cpu.getRegPC();
                break;
            default:
                result = 0;
        }
        return result;
    }

    private void setRegisterValue(CPU cpu, String reg, int value) {
        switch (reg) {
            case "A":
                cpu.setAccA(value & 0xFF);
                break;
            case "B":
                cpu.setAccB(value & 0xFF);
                break;
            case "D":
                cpu.setAccD(value & 0xFFFF);
                break;
            case "X":
                cpu.setRegX(value & 0xFFFF);
                break;
            case "Y":
                cpu.setRegY(value & 0xFFFF);
                break;
            case "U":
                cpu.setRegU(value & 0xFFFF);
                break;
            case "S":
                cpu.setRegS(value & 0xFFFF);
                break;
            case "PC":
                cpu.setRegPC(value & 0xFFFF);
                break;
            default:
                break;
        }
    }

    @Override
    public String getMnemonic() { return mnemonic; }

    @Override
    public int getSize() { return 2; } // opcode + postbyte
}
