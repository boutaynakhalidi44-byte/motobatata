package motorola.decoder;

import java.util.HashMap;
import java.util.Map;
import motorola.cpu.CPU;
import motorola.instructions.*;

/**
 * Décodeur d'instructions pour le processeur Motorola 6809
 */
public class InstructionDecoder {

    private static final Map<Integer, Instruction> instructions = new HashMap<>();

    static {
        // ================================================
        // Instructions sans opérande (Inherent)
        // ================================================
        instructions.put(0x12, new NOPInstruction());
        
        // LEA instructions - Load Effective Address (generic INDEXED mode to decode register from postbyte)
        instructions.put(0x30, new LEAXInstruction("LEAX", LEAXInstruction.INDEXED));
        instructions.put(0x31, new LEAYInstruction("LEAY", LEAYInstruction.INDEXED));
        instructions.put(0x32, new LEASInstruction("LEAS", LEASInstruction.INDEXED));
        instructions.put(0x33, new LEAUInstruction("LEAU", LEAUInstruction.INDEXED));
        
    // RTS/RTI removed per request
        instructions.put(0x3A, new ABXInstruction());
        instructions.put(0x3D, new MULInstruction());
        instructions.put(0x19, new DAAInstruction());
        instructions.put(0x3F, new HALTInstruction("HALT"));  // Arrête le CPU
        
        // EXG - Exchange (opcode 0x1E, postbyte contient les registres à échanger)
        // Ce n'est qu'un marqueur, le vrai décodage se fait dans decode()
        instructions.put(0x1E, new EXGInstruction("", ""));  // Sera remplacé par decode()
        
        // TFR - Transfer (opcode 0x1F, postbyte contient les registres src et dest)
        // Ce n'est qu'un marqueur, le vrai décodage se fait dans decode()
        instructions.put(0x1F, new TFRInstruction("", ""));  // Sera remplacé par decode()

        // ================================================
        // Instructions ADD (8B, 9B, AB, BB pour A)
        // ================================================
        instructions.put(0x8B, new AddInstruction("ADDA", true, AddInstruction.IMM8));
        instructions.put(0x9B, new AddInstruction("ADDA", true, AddInstruction.DIRECT));
        instructions.put(0xAB, new AddInstruction("ADDA", true, AddInstruction.INDEXED));
        instructions.put(0xBB, new AddInstruction("ADDA", true, AddInstruction.EXTENDED));
        
        // ADDB (CB, DB, EB, FB)
        instructions.put(0xCB, new AddInstruction("ADDB", false, AddInstruction.IMM8));
        instructions.put(0xDB, new AddInstruction("ADDB", false, AddInstruction.DIRECT));
        instructions.put(0xEB, new AddInstruction("ADDB", false, AddInstruction.INDEXED));
        instructions.put(0xFB, new AddInstruction("ADDB", false, AddInstruction.EXTENDED));

        // ================================================
        // Instructions SUB (80, 90, A0, B0 pour A)
        // ================================================
        instructions.put(0x80, new SUBInstruction("SUBA", true, SUBInstruction.IMM8));
        instructions.put(0x90, new SUBInstruction("SUBA", true, SUBInstruction.DIRECT));
        instructions.put(0xA0, new SUBInstruction("SUBA", true, SUBInstruction.INDEXED));
        instructions.put(0xB0, new SUBInstruction("SUBA", true, SUBInstruction.EXTENDED));
        
        // SUBB (C0, D0, E0, F0)
        instructions.put(0xC0, new SUBInstruction("SUBB", false, SUBInstruction.IMM8));
        instructions.put(0xD0, new SUBInstruction("SUBB", false, SUBInstruction.DIRECT));
        instructions.put(0xE0, new SUBInstruction("SUBB", false, SUBInstruction.INDEXED));
        instructions.put(0xF0, new SUBInstruction("SUBB", false, SUBInstruction.EXTENDED));

        // ================================================
        // Instructions ADC (89, 99, A9, B9 pour A)
        // ================================================
        instructions.put(0x89, new ADCInstruction("ADCA", true, ADCInstruction.IMM8));
        instructions.put(0x99, new ADCInstruction("ADCA", true, ADCInstruction.DIRECT));
        instructions.put(0xA9, new ADCInstruction("ADCA", true, ADCInstruction.INDEXED));
        instructions.put(0xB9, new ADCInstruction("ADCA", true, ADCInstruction.EXTENDED));
        
        // ADCB (C9, D9, E9, F9)
        instructions.put(0xC9, new ADCInstruction("ADCB", false, ADCInstruction.IMM8));
        instructions.put(0xD9, new ADCInstruction("ADCB", false, ADCInstruction.DIRECT));
        instructions.put(0xE9, new ADCInstruction("ADCB", false, ADCInstruction.INDEXED));
        instructions.put(0xF9, new ADCInstruction("ADCB", false, ADCInstruction.EXTENDED));

        // ================================================
        // Instructions SBC (82, 92, A2, B2 pour A)
        // ================================================
        instructions.put(0x82, new SBCInstruction("SBCA", true, SBCInstruction.IMM8));
        instructions.put(0x92, new SBCInstruction("SBCA", true, SBCInstruction.DIRECT));
        instructions.put(0xA2, new SBCInstruction("SBCA", true, SBCInstruction.INDEXED));
        instructions.put(0xB2, new SBCInstruction("SBCA", true, SBCInstruction.EXTENDED));
        
        // SBCB (C2, D2, E2, F2)
        instructions.put(0xC2, new SBCInstruction("SBCB", false, SBCInstruction.IMM8));
        instructions.put(0xD2, new SBCInstruction("SBCB", false, SBCInstruction.DIRECT));
        instructions.put(0xE2, new SBCInstruction("SBCB", false, SBCInstruction.INDEXED));
        instructions.put(0xF2, new SBCInstruction("SBCB", false, SBCInstruction.EXTENDED));

        // ================================================
        // Instructions AND (84, 94, A4, B4 pour A)
        // ================================================
        instructions.put(0x84, new ANDInstruction("ANDA", true, ANDInstruction.IMM8));
        instructions.put(0x94, new ANDInstruction("ANDA", true, ANDInstruction.DIRECT));
        instructions.put(0xA4, new ANDInstruction("ANDA", true, ANDInstruction.INDEXED));
        instructions.put(0xB4, new ANDInstruction("ANDA", true, ANDInstruction.EXTENDED));
        
        // ANDB (C4, D4, E4, F4)
        instructions.put(0xC4, new ANDInstruction("ANDB", false, ANDInstruction.IMM8));
        instructions.put(0xD4, new ANDInstruction("ANDB", false, ANDInstruction.DIRECT));
        instructions.put(0xE4, new ANDInstruction("ANDB", false, ANDInstruction.INDEXED));
        instructions.put(0xF4, new ANDInstruction("ANDB", false, ANDInstruction.EXTENDED));

        // ================================================
        // Instructions OR (8A, 9A, AA, BA pour A)
        // ================================================
        instructions.put(0x8A, new ORInstruction("ORA", true, ORInstruction.IMM8));
        instructions.put(0x9A, new ORInstruction("ORA", true, ORInstruction.DIRECT));
        instructions.put(0xAA, new ORInstruction("ORA", true, ORInstruction.INDEXED));
        instructions.put(0xBA, new ORInstruction("ORA", true, ORInstruction.EXTENDED));
        
        // ORB (CA, DA, EA, FA)
        instructions.put(0xCA, new ORInstruction("ORB", false, ORInstruction.IMM8));
        instructions.put(0xDA, new ORInstruction("ORB", false, ORInstruction.DIRECT));
        instructions.put(0xEA, new ORInstruction("ORB", false, ORInstruction.INDEXED));
        instructions.put(0xFA, new ORInstruction("ORB", false, ORInstruction.EXTENDED));

        // ================================================
        // Instructions EOR (88, 98, A8, B8 pour A)
        // ================================================
        instructions.put(0x88, new EORInstruction("EORA", true, EORInstruction.IMM8));
        instructions.put(0x98, new EORInstruction("EORA", true, EORInstruction.DIRECT));
        instructions.put(0xA8, new EORInstruction("EORA", true, EORInstruction.INDEXED));
        instructions.put(0xB8, new EORInstruction("EORA", true, EORInstruction.EXTENDED));
        
        // EORB (C8, D8, E8, F8)
        instructions.put(0xC8, new EORInstruction("EORB", false, EORInstruction.IMM8));
        instructions.put(0xD8, new EORInstruction("EORB", false, EORInstruction.DIRECT));
        instructions.put(0xE8, new EORInstruction("EORB", false, EORInstruction.INDEXED));
        instructions.put(0xF8, new EORInstruction("EORB", false, EORInstruction.EXTENDED));

        // ================================================
        // Instructions CMP (81, 91, A1, B1 pour A)
        // ================================================
        instructions.put(0x81, new CMPInstruction("CMPA", 0, CMPInstruction.IMM8));
        instructions.put(0x91, new CMPInstruction("CMPA", 0, CMPInstruction.DIRECT));
        instructions.put(0xA1, new CMPInstruction("CMPA", 0, CMPInstruction.INDEXED));
        instructions.put(0xB1, new CMPInstruction("CMPA", 0, CMPInstruction.EXTENDED));
        
        // CMPB (C1, D1, E1, F1)
        instructions.put(0xC1, new CMPInstruction("CMPB", 1, CMPInstruction.IMM8));
        instructions.put(0xD1, new CMPInstruction("CMPB", 1, CMPInstruction.DIRECT));
        instructions.put(0xE1, new CMPInstruction("CMPB", 1, CMPInstruction.INDEXED));
        instructions.put(0xF1, new CMPInstruction("CMPB", 1, CMPInstruction.EXTENDED));

        // ================================================
        // Instructions BIT (85, 95, A5, B5 pour A)
        // ================================================
        instructions.put(0x85, new BITInstruction("BITA", 0, BITInstruction.IMM8));
        instructions.put(0x95, new BITInstruction("BITA", 0, BITInstruction.DIRECT));
        instructions.put(0xA5, new BITInstruction("BITA", 0, BITInstruction.INDEXED));
        instructions.put(0xB5, new BITInstruction("BITA", 0, BITInstruction.EXTENDED));
        
        // BITB (C5, D5, E5, F5)
        instructions.put(0xC5, new BITInstruction("BITB", 1, BITInstruction.IMM8));
        instructions.put(0xD5, new BITInstruction("BITB", 1, BITInstruction.DIRECT));
        instructions.put(0xE5, new BITInstruction("BITB", 1, BITInstruction.INDEXED));
        instructions.put(0xF5, new BITInstruction("BITB", 1, BITInstruction.EXTENDED));

        // ================================================
        // Instructions LD (86, 96, A6, B6 pour A)
        // ================================================
        instructions.put(0x86, new LDInstruction("LDA", true, LDInstruction.IMM8));
        instructions.put(0x96, new LDInstruction("LDA", true, LDInstruction.DIRECT));
        instructions.put(0xA6, new LDInstruction("LDA", true, LDInstruction.INDEXED));
        instructions.put(0xB6, new LDInstruction("LDA", true, LDInstruction.EXTENDED));
        
        // LDB (C6, D6, E6, F6)
        instructions.put(0xC6, new LDInstruction("LDB", false, LDInstruction.IMM8));
        instructions.put(0xD6, new LDInstruction("LDB", false, LDInstruction.DIRECT));
        instructions.put(0xE6, new LDInstruction("LDB", false, LDInstruction.INDEXED));
        instructions.put(0xF6, new LDInstruction("LDB", false, LDInstruction.EXTENDED));

        // ================================================
        // Instructions ST (97, A7, B7 pour A)
        // ================================================
        instructions.put(0x97, new STInstruction("STA", true, STInstruction.DIRECT));
        instructions.put(0xA7, new STInstruction("STA", true, STInstruction.INDEXED));
        instructions.put(0xB7, new STInstruction("STA", true, STInstruction.EXTENDED));
        
        // STB (D7, E7, F7)
        instructions.put(0xD7, new STInstruction("STB", false, STInstruction.DIRECT));
        instructions.put(0xE7, new STInstruction("STB", false, STInstruction.INDEXED));
        instructions.put(0xF7, new STInstruction("STB", false, STInstruction.EXTENDED));

        // ================================================
        // Instructions unaires (CLR, INC, DEC, COM, NEG, TST)
        // ================================================
        // NEG (00, 60, 70)
        instructions.put(0x00, new NEGInstruction("NEG", NEGInstruction.DIRECT));
        instructions.put(0x60, new NEGInstruction("NEG", NEGInstruction.INDEXED_X));
        instructions.put(0x70, new NEGInstruction("NEG", NEGInstruction.EXTENDED));
        instructions.put(0x40, new NEGInstruction("NEGA", NEGInstruction.DIRECT));
        instructions.put(0x50, new NEGInstruction("NEGB", NEGInstruction.DIRECT));
        
    // COM instruction removed
        
        // DEC (0A, 6A, 7A)
        instructions.put(0x0A, new DECInstruction("DEC", DECInstruction.DIRECT));
        instructions.put(0x6A, new DECInstruction("DEC", DECInstruction.INDEXED_X));
        instructions.put(0x7A, new DECInstruction("DEC", DECInstruction.EXTENDED));
        instructions.put(0x4A, new DECInstruction("DECA", DECInstruction.DIRECT));
        instructions.put(0x5B, new DECInstruction("DECB", DECInstruction.DIRECT));
        
        // INC (0C, 6C, 7C)
        instructions.put(0x0C, new INCInstruction("INC", INCInstruction.DIRECT));
        instructions.put(0x6C, new INCInstruction("INC", INCInstruction.INDEXED_X));
        instructions.put(0x7C, new INCInstruction("INC", INCInstruction.EXTENDED));
        instructions.put(0x4C, new INCInstruction("INCA", INCInstruction.DIRECT));
        instructions.put(0x5C, new INCInstruction("INCB", INCInstruction.DIRECT));
        
        // TST (0D, 6D, 7D)
        instructions.put(0x0D, new TSTInstruction("TST", TSTInstruction.DIRECT));
        instructions.put(0x6D, new TSTInstruction("TST", TSTInstruction.INDEXED_X));
        instructions.put(0x7D, new TSTInstruction("TST", TSTInstruction.EXTENDED));
        instructions.put(0x4D, new TSTInstruction("TSTA", TSTInstruction.DIRECT));
        instructions.put(0x5D, new TSTInstruction("TSTB", TSTInstruction.DIRECT));
        
        // CLR (0F, 6F, 7F)
        instructions.put(0x0F, new CLRInstruction("CLR", CLRInstruction.DIRECT));
        instructions.put(0x6F, new CLRInstruction("CLR", CLRInstruction.INDEXED_X));
        instructions.put(0x7F, new CLRInstruction("CLR", CLRInstruction.EXTENDED));
        instructions.put(0x4F, new CLRInstruction("CLRA", CLRInstruction.DIRECT));
        instructions.put(0x5F, new CLRInstruction("CLRB", CLRInstruction.DIRECT));

        // ================================================
        // Instructions 16-bit (D, X, Y, S, U)
        // ================================================
        // LDD (CC, DC, EC, FC)
        instructions.put(0xCC, new LDDInstruction("LDD", LDDInstruction.IMM16));
        instructions.put(0xDC, new LDDInstruction("LDD", LDDInstruction.DIRECT));
        instructions.put(0xEC, new LDDInstruction("LDD", LDDInstruction.INDEXED));
        instructions.put(0xFC, new LDDInstruction("LDD", LDDInstruction.EXTENDED));
        
        // STD (10DD, 10ED, 10FD)
        instructions.put(0x10DD, new STDInstruction("STD", STDInstruction.DIRECT));
        instructions.put(0x10ED, new STDInstruction("STD", STDInstruction.INDEXED));
        instructions.put(0x10FD, new STDInstruction("STD", STDInstruction.EXTENDED));
        
        // ADDD (C3, D3, E3, F3)
        instructions.put(0xC3, new ADDDInstruction("ADDD", ADDDInstruction.IMM16));
        instructions.put(0xD3, new ADDDInstruction("ADDD", ADDDInstruction.DIRECT));
        instructions.put(0xE3, new ADDDInstruction("ADDD", ADDDInstruction.INDEXED));
        instructions.put(0xF3, new ADDDInstruction("ADDD", ADDDInstruction.EXTENDED));
        
        // SUBD (83, 93, A3, B3)
        instructions.put(0x83, new SUBDInstruction("SUBD", SUBDInstruction.IMM16));
        instructions.put(0x93, new SUBDInstruction("SUBD", SUBDInstruction.DIRECT));
        instructions.put(0xA3, new SUBDInstruction("SUBD", SUBDInstruction.INDEXED));
        instructions.put(0xB3, new SUBDInstruction("SUBD", SUBDInstruction.EXTENDED));
        
        // CMPD (1083, 1093, 10A3, 10B3)
        instructions.put(0x1083, new CMPDInstruction("CMPD", CMPDInstruction.IMM16));
        instructions.put(0x1093, new CMPDInstruction("CMPD", CMPDInstruction.DIRECT));
        instructions.put(0x10A3, new CMPDInstruction("CMPD", CMPDInstruction.INDEXED));
        instructions.put(0x10B3, new CMPDInstruction("CMPD", CMPDInstruction.EXTENDED));
        
        // LDX (8E, 9E, AE, BE)
        instructions.put(0x8E, new LDXInstruction("LDX", LDXInstruction.IMM16));
        instructions.put(0x9E, new LDXInstruction("LDX", LDXInstruction.DIRECT));
        instructions.put(0xAE, new LDXInstruction("LDX", LDXInstruction.INDEXED));
        instructions.put(0xBE, new LDXInstruction("LDX", LDXInstruction.EXTENDED));
        
        // STX (10FE, 109F, 10AF, 10BF)
        instructions.put(0x10DF, new STXInstruction("STX", STXInstruction.DIRECT));
        instructions.put(0x10EF, new STXInstruction("STX", STXInstruction.INDEXED));
        instructions.put(0x10FF, new STXInstruction("STX", STXInstruction.EXTENDED));
        
        // LDY (10CE, 109E, 10AE, 10BE)
        instructions.put(0x10CE, new LDYInstruction("LDY", LDYInstruction.IMM16));
        instructions.put(0x109E, new LDYInstruction("LDY", LDYInstruction.DIRECT));
        instructions.put(0x10AE, new LDYInstruction("LDY", LDYInstruction.INDEXED));
        instructions.put(0x10BE, new LDYInstruction("LDY", LDYInstruction.EXTENDED));
        
        // STY (10DF, 10EF, 10FF)
        instructions.put(0x10DF, new STYInstruction("STY", STYInstruction.DIRECT));
        instructions.put(0x10EF, new STYInstruction("STY", STYInstruction.INDEXED));
        instructions.put(0x10FF, new STYInstruction("STY", STYInstruction.EXTENDED));
        
        // LDS (10FE, 10DF, 10EF, 10FF)
        instructions.put(0x10FE, new LDSInstruction("LDS", LDSInstruction.IMM16));
        instructions.put(0x109F, new LDSInstruction("LDS", LDSInstruction.DIRECT));
        instructions.put(0x10AF, new LDSInstruction("LDS", LDSInstruction.INDEXED));
        instructions.put(0x10BF, new LDSInstruction("LDS", LDSInstruction.EXTENDED));
        
        // STS (10DF, 10EF, 10FF)
        instructions.put(0x10D7, new STSInstruction("STS", STSInstruction.DIRECT));
        instructions.put(0x10E7, new STSInstruction("STS", STSInstruction.INDEXED));
        instructions.put(0x10F7, new STSInstruction("STS", STSInstruction.EXTENDED));
        
        // LDU (CE, DE, EE, FE)
        instructions.put(0xCE, new LDUInstruction("LDU", LDUInstruction.IMM16));
        instructions.put(0xDE, new LDUInstruction("LDU", LDUInstruction.DIRECT));
        instructions.put(0xEE, new LDUInstruction("LDU", LDUInstruction.INDEXED));
        instructions.put(0xFE, new LDUInstruction("LDU", LDUInstruction.EXTENDED));
        
        // STU (DF, EF, FF)
        instructions.put(0xDF, new STUInstruction("STU", STUInstruction.DIRECT));
        instructions.put(0xEF, new STUInstruction("STU", STUInstruction.INDEXED));
        instructions.put(0xFF, new STUInstruction("STU", STUInstruction.EXTENDED));

        // ================================================
        // Branchements relatifs 8 bits (2x)
        // ================================================
        instructions.put(0x20, new BRAInstruction());
        instructions.put(0x21, new BRNInstruction());
        instructions.put(0x22, new BHIInstruction());
        instructions.put(0x23, new BLSInstruction());
        instructions.put(0x24, new BCCInstruction());
        instructions.put(0x25, new BCSInstruction());
        instructions.put(0x26, new BNEInstruction());
        instructions.put(0x27, new BEQInstruction());
        instructions.put(0x28, new BVCInstruction());
        instructions.put(0x29, new BVSInstruction());
        instructions.put(0x2A, new BPLInstruction());
        instructions.put(0x2B, new BMIInstruction());
        instructions.put(0x2C, new BGEInstruction());
        instructions.put(0x2D, new BLTInstruction());
        instructions.put(0x2E, new BGTInstruction());
        instructions.put(0x2F, new BLEInstruction());
        instructions.put(0x8D, new BSRInstruction());

    // JMP and JSR removed



    }
    
    /**
     * Convertit un code de registre (0-8) en nom de registre
     */
    private static String getRegisterName(int code) {
        switch (code) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "CC";
            case 3: return "DP";
            case 4: return "D";
            case 5: return "X";
            case 6: return "Y";
            case 7: return "U";
            case 8: return "S";
            default: return "";
        }
    }
    
    /**
     * Décode l'instruction à partir de l'opcode
     * @param opcode L'opcode de l'instruction
     * @param cpu Le processeur
     * @return L'instruction décodée, ou null si l'opcode est inconnu
     */
    public static Instruction decode(int opcode, CPU cpu) {
        // Gestion de EXG (0x1E) - Exchange registers
        if (opcode == 0x1E) {
            int postbyte = cpu.fetchByte() & 0xFF;
            int reg1Code = (postbyte >> 4) & 0xF;
            int reg2Code = postbyte & 0xF;
            String reg1 = getRegisterName(reg1Code);
            String reg2 = getRegisterName(reg2Code);
            return new EXGInstruction(reg1, reg2);
        }
        
        // Gestion de TFR (0x1F) - Transfer register
        if (opcode == 0x1F) {
            int postbyte = cpu.fetchByte() & 0xFF;
            int srcCode = (postbyte >> 4) & 0xF;
            int destCode = postbyte & 0xF;
            String src = getRegisterName(srcCode);
            String dest = getRegisterName(destCode);
            return new TFRInstruction(src, dest);
        }
        
        // Gestion du mode EXTENDED INDIRECT (marqueur 0x04)
        if (opcode == 0x04) {
            //System.out.println("DEBUG Decoder: EXTENDED INDIRECT marker 0x04 detected");
            cpu.setExtendedIndirectMode(true);
            int nextOpcode = cpu.fetchByte() & 0xFF;
            //System.out.println("DEBUG Decoder: Setting flag, next opcode=0x" + String.format("%02X", nextOpcode));
            
            // Gérer les préfixes page 2 (0x10) et page 3 (0x11) après le marqueur indirect
            if (nextOpcode == 0x10) {
                int pageOpcode = cpu.fetchByte() & 0xFF;
                int fullOpcode = (nextOpcode << 8) | pageOpcode;
                //System.out.println("DEBUG Decoder: Page 2 prefix detected, full opcode=0x" + String.format("%04X", fullOpcode));
                return instructions.get(fullOpcode);
            } else if (nextOpcode == 0x11) {
                int pageOpcode = cpu.fetchByte() & 0xFF;
                int fullOpcode = (nextOpcode << 8) | pageOpcode;
                //System.out.println("DEBUG Decoder: Page 3 prefix detected, full opcode=0x" + String.format("%04X", fullOpcode));
                return instructions.get(fullOpcode);
            }
            
            // Sinon, c'est un opcode simple
            return instructions.get(nextOpcode);
        }
        
        // Gestion des opcodes avec préfixe 0x10 (Page 2)
        if (opcode == 0x10) {
            int nextByte = cpu.fetchByte() & 0xFF;
            int fullOpcode = (opcode << 8) | nextByte;
            return instructions.get(fullOpcode);
        }
        
        // Gestion des opcodes avec préfixe 0x11 (Page 3)
        if (opcode == 0x11) {
            int nextByte = cpu.fetchByte() & 0xFF;
            int fullOpcode = (opcode << 8) | nextByte;
            return instructions.get(fullOpcode);
        }

        return instructions.get(opcode);
    }

    /**
     * Enregistre une instruction dans le décodeur
     * @param opcode L'opcode de l'instruction
     * @param instruction L'instruction à enregistrer
     */
    public static void registerInstruction(int opcode, Instruction instruction) {
        instructions.put(opcode, instruction);
    }
}
