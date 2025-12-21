package motobatata.addressing;

import motobatata.cpu.CPU;

/**
 * Classe utilitaire pour calculer les adresses selon les différents modes d'adressage
 */
public class AddressingMode {

    public static int immediate8(CPU cpu) {
        return cpu.fetchByte();
    }

    public static int immediate16(CPU cpu) {
        return cpu.fetchWord();
    }

    public static int direct(CPU cpu) {
        int offset = cpu.fetchByte();
        return ((cpu.getRegDP() << 8) | offset) & 0xFFFF;
    }

    public static int extended(CPU cpu) {
        return cpu.fetchWord();
    }

    public static int relative8(CPU cpu) {
        int offset = (byte) cpu.fetchByte(); 
        return (cpu.getRegPC() + offset) & 0xFFFF;
    }

    public static int relative16(CPU cpu) {
        int offset = (short) cpu.fetchWord(); 
        return (cpu.getRegPC() + offset) & 0xFFFF;
    }


    public static int indexedX(CPU cpu) {
        int offset = (byte) cpu.fetchByte();
        return (cpu.getRegX() + offset) & 0xFFFF;
    }

    public static int indexedY(CPU cpu) {
        int offset = (byte) cpu.fetchByte();
        return (cpu.getRegY() + offset) & 0xFFFF;
    }

    public static int indexedU(CPU cpu) {
        int offset = (byte) cpu.fetchByte();
        return (cpu.getRegU() + offset) & 0xFFFF;
    }

    public static int indexedS(CPU cpu) {
        int offset = (byte) cpu.fetchByte();
        return (cpu.getRegS() + offset) & 0xFFFF;
    }

    public static int indexedPC(CPU cpu) {
        int offset = (byte) cpu.fetchByte();
        return (cpu.getRegPC() + offset) & 0xFFFF;
    }
}

