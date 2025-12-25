package motorola.addressing;

import motorola.cpu.CPU;
import motorola.memory.Memory;

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
        int dp = cpu.getRegDP();
        int address = ((dp << 8) | offset) & 0xFFFF;
        return address;
    }

    public static int extended(CPU cpu) {
        return cpu.fetchWord();
    }

    public static int extendedIndirect(CPU cpu, Memory memory) {
        // Récupère l'adresse (16-bit)
        int addressOfAddress = cpu.fetchWord();
        //System.out.println("DEBUG AddressingMode.extendedIndirect: pointer address=0x" + String.format("%04X", addressOfAddress));
        // Utilise cette adresse comme pointeur pour récupérer l'adresse finale
        int finalAddress = memory.readWord(addressOfAddress);
        //System.out.println("DEBUG AddressingMode.extendedIndirect: final address=0x" + String.format("%04X", finalAddress));
        return finalAddress & 0xFFFF;
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
        int postbyte = cpu.fetchByte();
        return decodeIndexedAddress(cpu, postbyte, cpu.getRegX(), 0);
    }

    public static int indexedY(CPU cpu) {
        int postbyte = cpu.fetchByte();
        return decodeIndexedAddress(cpu, postbyte, cpu.getRegY(), 1);
    }

    public static int indexedU(CPU cpu) {
        int postbyte = cpu.fetchByte();
        return decodeIndexedAddress(cpu, postbyte, cpu.getRegU(), 2);
    }

    public static int indexedS(CPU cpu) {
        int postbyte = cpu.fetchByte();
        return decodeIndexedAddress(cpu, postbyte, cpu.getRegS(), 3);
    }

    public static int indexedPC(CPU cpu) {
        int postbyte = cpu.fetchByte();
        return decodeIndexedAddress(cpu, postbyte, cpu.getRegPC(), 4);
    }
    
    /**
     * Fonction générique pour mode INDEXED qui détermine le registre depuis le postbyte
     * Utilisée pour les instructions comme ADDB ,Y où le registre est dans le postbyte
     */
    public static int indexedGeneric(CPU cpu) {
        int postbyte = cpu.fetchByte();
        int regIndex = (postbyte >> 6) & 0x03;  // Extraire RR (bits 7-6)

        int regValue;
        switch (regIndex) {
            case 0: regValue = cpu.getRegX(); break;
            case 1: regValue = cpu.getRegY(); break;
            case 2: regValue = cpu.getRegU(); break;
            case 3: regValue = cpu.getRegS(); break;
            default: regValue = cpu.getRegX();
        }

        int address = decodeIndexedAddress(cpu, postbyte, regValue, regIndex);
        System.out.printf("DEBUG indexedGeneric: postbyte=0x%02X, regIndex=%d, regValue=0x%04X, calculated_address=0x%04X\n", 
            postbyte, regIndex, regValue, address);

        // Si le postbyte indique un mode indirect indexé sans offset ([,X]) ou avec offset
        // (E bit = 1 and mode nibble == 0x04 for no-offset indirect), alors déréférencer
        // l'adresse calculée pour obtenir l'adresse finale (16-bit pointer stored at address).
        boolean isIndirectOrSpecial = (postbyte & 0x10) != 0;
        int mode = postbyte & 0x0F;
        if (isIndirectOrSpecial && mode == 0x04) {
            // Lire le mot pointeur à l'adresse indexée
            int finalAddr = cpu.getMemory().readWord(address) & 0xFFFF;
            System.out.printf("DEBUG indexedGeneric: indirect indexed -> pointer at 0x%04X = 0x%04X\n", address, finalAddr);
            return finalAddr;
        }

        return address;
    }
    
    /**
     * Décode le postbyte d'adressage indexé 6809
     * regIndex: 0=X, 1=Y, 2=U, 3=S, 4=PC
     */
    private static int decodeIndexedAddress(CPU cpu, int postbyte, int regValue, int regIndex) {
        // Format 6809: RR|I|BBBBB (bits 7-6: RR, bit 4: I, bits 3-0: BBBBB)
        // Si I=0 (bit 4=0): BBBBB = 5-bit signed offset (bits 4-0 = OOOOO)
        // Si I=1 (bit 4=1): BBBBB indique le mode spécial (bits 3-0 = SSSS)
        
        // Vérifier le bit 4 (E bit pour indirect/mode spécial)
        boolean isIndirectOrSpecial = (postbyte & 0x10) != 0;
        
        int address = 0;
        
        // Vérifier si c'est un mode accumulator offset (bits 3-0 = 0x05 ou 0x06 AND E=1 i.e. bit 4=1)
        // Accumulator offset requires the E bit to differentiate from 4-bit offsets
        if (isIndirectOrSpecial && ((postbyte & 0x0F) == 0x05 || (postbyte & 0x0F) == 0x06)) {
            // Mode accumulator: A,X=0x96, B,X=0x95, A,Y=0xB6, B,Y=0xB5, etc.
            int accum = (postbyte & 0x0F) == 0x06 ? cpu.getAccA() : cpu.getAccB();
            address = (regValue + accum) & 0xFFFF;
            return address;
        }
        
        if (!isIndirectOrSpecial) {
            // E=0: bits 3-0 = OOOO (4-bit signed offset)
            // Range: -8 to +7
            int off4 = postbyte & 0x0F;  // Only 4 bits
            if ((off4 & 0x08) != 0) off4 |= 0xFFFFFFF8; // Sign-extend bit 3
            // System.out.printf("DEBUG AddressingMode.decodeIndexedAddress: postbyte=0x%02X, off4_raw=0x%02X, off4_signed=%d, regValue=0x%04X\n", 
            //     postbyte, postbyte & 0x0F, off4, regValue);
            address = (regValue + off4) & 0xFFFF;
            // System.out.printf("DEBUG AddressingMode.decodeIndexedAddress: final address=0x%04X\n", address);
        } else {
            // I=1: bits 3-0 indiquent le mode spécial
            int mode = postbyte & 0x0F;  // Extract only bits 3-0 when E=1
            
            switch (mode) {
                case 0x00: // RR|1|0000 = Post-increment R+
                    address = regValue & 0xFFFF;
                    updateRegister(cpu, regIndex, (regValue + 1) & 0xFFFF);
                    break;
                case 0x01: // RR|1|0001 = Post-increment R++
                    address = regValue & 0xFFFF;
                    updateRegister(cpu, regIndex, (regValue + 2) & 0xFFFF);
                    break;
                case 0x0E: // RR|1|1110 = Pre-decrement --R
                    updateRegister(cpu, regIndex, (regValue - 2) & 0xFFFF);
                    address = (regValue - 2) & 0xFFFF;
                    break;
                case 0x0F: // RR|1|1111 = Pre-decrement -R
                    updateRegister(cpu, regIndex, (regValue - 1) & 0xFFFF);
                    address = (regValue - 1) & 0xFFFF;
                    break;
                case 0x08: // RR|1|1000 = 8-bit offset
                    int off8 = cpu.fetchByte();
                    if ((off8 & 0x80) != 0) off8 |= 0xFFFFFF00; // Sign-extend
                    address = (regValue + off8) & 0xFFFF;
                    break;
                case 0x09: // RR|1|1001 = 16-bit offset
                    int hi = cpu.fetchByte();
                    int lo = cpu.fetchByte();
                    int off16 = ((hi << 8) | lo) & 0xFFFF;
                    if ((off16 & 0x8000) != 0) off16 |= 0xFFFF0000; // Sign-extend
                    address = (regValue + off16) & 0xFFFF;
                    break;
                default:
                    address = regValue & 0xFFFF;
            }
        }
        
        return address;
    }
    
    private static void updateRegister(CPU cpu, int regIndex, int value) {
        switch (regIndex) {
            case 0: cpu.setRegX(value); break;
            case 1: cpu.setRegY(value); break;
            case 2: cpu.setRegU(value); break;
            case 3: cpu.setRegS(value); break;
            case 4: cpu.setRegPC(value); break;
        }
    }
}
