package motobatata.decoder;

import java.util.HashMap;
import java.util.Map;
import motobatata.cpu.CPU;
import motobatata.instructions.*;

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
        instructions.put(0x39, new RTSInstruction());
        instructions.put(0x3B, new RTIInstruction());
        instructions.put(0x3A, new ABXInstruction());
        instructions.put(0x3D, new MULInstruction());
        instructions.put(0x19, new DAAInstruction());

        // ================================================
        // Instructions ADD (8B, 9B, AB, BB pour A)
        // ================================================
        instructions.put(0x8B, new AddInstruction("ADDA", true, AddInstruction.IMM8));
        instructions.put(0x9B, new AddInstruction("ADDA", true, AddInstruction.DIRECT));
        instructions.put(0xAB, new AddInstruction("ADDA", true, AddInstruction.INDEXED_X));
        instructions.put(0xBB, new AddInstruction("ADDA", true, AddInstruction.EXTENDED));
        
        // ADDB (CB, DB, EB, FB)
        instructions.put(0xCB, new AddInstruction("ADDB", false, AddInstruction.IMM8));
        instructions.put(0xDB, new AddInstruction("ADDB", false, AddInstruction.DIRECT));
        instructions.put(0xEB, new AddInstruction("ADDB", false, AddInstruction.INDEXED_X));
        instructions.put(0xFB, new AddInstruction("ADDB", false, AddInstruction.EXTENDED));

        // ================================================
        // Instructions SUB (80, 90, A0, B0 pour A)
        // ================================================
        instructions.put(0x80, new SUBInstruction("SUBA", true, SUBInstruction.IMM8));
        instructions.put(0x90, new SUBInstruction("SUBA", true, SUBInstruction.DIRECT));
        instructions.put(0xA0, new SUBInstruction("SUBA", true, SUBInstruction.INDEXED_X));
        instructions.put(0xB0, new SUBInstruction("SUBA", true, SUBInstruction.EXTENDED));
        
        // SUBB (C0, D0, E0, F0)
        instructions.put(0xC0, new SUBInstruction("SUBB", false, SUBInstruction.IMM8));
        instructions.put(0xD0, new SUBInstruction("SUBB", false, SUBInstruction.DIRECT));
        instructions.put(0xE0, new SUBInstruction("SUBB", false, SUBInstruction.INDEXED_X));
        instructions.put(0xF0, new SUBInstruction("SUBB", false, SUBInstruction.EXTENDED));

        // ================================================
        // Instructions ADC (89, 99, A9, B9 pour A)
        // ================================================
        instructions.put(0x89, new ADCInstruction("ADCA", true, ADCInstruction.IMM8));
        instructions.put(0x99, new ADCInstruction("ADCA", true, ADCInstruction.DIRECT));
        instructions.put(0xA9, new ADCInstruction("ADCA", true, ADCInstruction.INDEXED_X));
        instructions.put(0xB9, new ADCInstruction("ADCA", true, ADCInstruction.EXTENDED));
        
        // ADCB (C9, D9, E9, F9)
        instructions.put(0xC9, new ADCInstruction("ADCB", false, ADCInstruction.IMM8));
        instructions.put(0xD9, new ADCInstruction("ADCB", false, ADCInstruction.DIRECT));
        instructions.put(0xE9, new ADCInstruction("ADCB", false, ADCInstruction.INDEXED_X));
        instructions.put(0xF9, new ADCInstruction("ADCB", false, ADCInstruction.EXTENDED));

        // ================================================
        // Instructions SBC (82, 92, A2, B2 pour A)
        // ================================================
        instructions.put(0x82, new SBCInstruction("SBCA", true, SBCInstruction.IMM8));
        instructions.put(0x92, new SBCInstruction("SBCA", true, SBCInstruction.DIRECT));
        instructions.put(0xA2, new SBCInstruction("SBCA", true, SBCInstruction.INDEXED_X));
        instructions.put(0xB2, new SBCInstruction("SBCA", true, SBCInstruction.EXTENDED));
        
        // SBCB (C2, D2, E2, F2)
        instructions.put(0xC2, new SBCInstruction("SBCB", false, SBCInstruction.IMM8));
        instructions.put(0xD2, new SBCInstruction("SBCB", false, SBCInstruction.DIRECT));
        instructions.put(0xE2, new SBCInstruction("SBCB", false, SBCInstruction.INDEXED_X));
        instructions.put(0xF2, new SBCInstruction("SBCB", false, SBCInstruction.EXTENDED));

        // ================================================
        // Instructions AND (84, 94, A4, B4 pour A)
        // ================================================
        instructions.put(0x84, new ANDInstruction("ANDA", true, ANDInstruction.IMM8));
        instructions.put(0x94, new ANDInstruction("ANDA", true, ANDInstruction.DIRECT));
        instructions.put(0xA4, new ANDInstruction("ANDA", true, ANDInstruction.INDEXED_X));
        instructions.put(0xB4, new ANDInstruction("ANDA", true, ANDInstruction.EXTENDED));
        
        // ANDB (C4, D4, E4, F4)
        instructions.put(0xC4, new ANDInstruction("ANDB", false, ANDInstruction.IMM8));
        instructions.put(0xD4, new ANDInstruction("ANDB", false, ANDInstruction.DIRECT));
        instructions.put(0xE4, new ANDInstruction("ANDB", false, ANDInstruction.INDEXED_X));
        instructions.put(0xF4, new ANDInstruction("ANDB", false, ANDInstruction.EXTENDED));

        // ================================================
        // Instructions OR (8A, 9A, AA, BA pour A)
        // ================================================
        instructions.put(0x8A, new ORInstruction("ORA", true, ORInstruction.IMM8));
        instructions.put(0x9A, new ORInstruction("ORA", true, ORInstruction.DIRECT));
        instructions.put(0xAA, new ORInstruction("ORA", true, ORInstruction.INDEXED_X));
        instructions.put(0xBA, new ORInstruction("ORA", true, ORInstruction.EXTENDED));
        
        // ORB (CA, DA, EA, FA)
        instructions.put(0xCA, new ORInstruction("ORB", false, ORInstruction.IMM8));
        instructions.put(0xDA, new ORInstruction("ORB", false, ORInstruction.DIRECT));
        instructions.put(0xEA, new ORInstruction("ORB", false, ORInstruction.INDEXED_X));
        instructions.put(0xFA, new ORInstruction("ORB", false, ORInstruction.EXTENDED));

        // ================================================
        // Instructions EOR (88, 98, A8, B8 pour A)
        // ================================================
        instructions.put(0x88, new EORInstruction("EORA", true, EORInstruction.IMM8));
        instructions.put(0x98, new EORInstruction("EORA", true, EORInstruction.DIRECT));
        instructions.put(0xA8, new EORInstruction("EORA", true, EORInstruction.INDEXED_X));
        instructions.put(0xB8, new EORInstruction("EORA", true, EORInstruction.EXTENDED));
        
        // EORB (C8, D8, E8, F8)
        instructions.put(0xC8, new EORInstruction("EORB", false, EORInstruction.IMM8));
        instructions.put(0xD8, new EORInstruction("EORB", false, EORInstruction.DIRECT));
        instructions.put(0xE8, new EORInstruction("EORB", false, EORInstruction.INDEXED_X));
        instructions.put(0xF8, new EORInstruction("EORB", false, EORInstruction.EXTENDED));

        // ================================================
        // Instructions CMP (81, 91, A1, B1 pour A)
        // ================================================
        instructions.put(0x81, new CMPInstruction("CMPA", 0, CMPInstruction.IMM8));
        instructions.put(0x91, new CMPInstruction("CMPA", 0, CMPInstruction.DIRECT));
        instructions.put(0xA1, new CMPInstruction("CMPA", 0, CMPInstruction.INDEXED_X));
        instructions.put(0xB1, new CMPInstruction("CMPA", 0, CMPInstruction.EXTENDED));
        
        // CMPB (C1, D1, E1, F1)
        instructions.put(0xC1, new CMPInstruction("CMPB", 1, CMPInstruction.IMM8));
        instructions.put(0xD1, new CMPInstruction("CMPB", 1, CMPInstruction.DIRECT));
        instructions.put(0xE1, new CMPInstruction("CMPB", 1, CMPInstruction.INDEXED_X));
        instructions.put(0xF1, new CMPInstruction("CMPB", 1, CMPInstruction.EXTENDED));

        // ================================================
        // Instructions BIT (85, 95, A5, B5 pour A)
        // ================================================
        instructions.put(0x85, new BITInstruction("BITA", 0, BITInstruction.IMM8));
        instructions.put(0x95, new BITInstruction("BITA", 0, BITInstruction.DIRECT));
        instructions.put(0xA5, new BITInstruction("BITA", 0, BITInstruction.INDEXED_X));
        instructions.put(0xB5, new BITInstruction("BITA", 0, BITInstruction.EXTENDED));
        
        // BITB (C5, D5, E5, F5)
        instructions.put(0xC5, new BITInstruction("BITB", 1, BITInstruction.IMM8));
        instructions.put(0xD5, new BITInstruction("BITB", 1, BITInstruction.DIRECT));
        instructions.put(0xE5, new BITInstruction("BITB", 1, BITInstruction.INDEXED_X));
        instructions.put(0xF5, new BITInstruction("BITB", 1, BITInstruction.EXTENDED));

        // ================================================
        // Instructions LD (86, 96, A6, B6 pour A)
        // ================================================
        instructions.put(0x86, new LDInstruction("LDA", true, LDInstruction.IMM8));
        instructions.put(0x96, new LDInstruction("LDA", true, LDInstruction.DIRECT));
        instructions.put(0xA6, new LDInstruction("LDA", true, LDInstruction.INDEXED_X));
        instructions.put(0xB6, new LDInstruction("LDA", true, LDInstruction.EXTENDED));
        
        // LDB (C6, D6, E6, F6)
        instructions.put(0xC6, new LDInstruction("LDB", false, LDInstruction.IMM8));
        instructions.put(0xD6, new LDInstruction("LDB", false, LDInstruction.DIRECT));
        instructions.put(0xE6, new LDInstruction("LDB", false, LDInstruction.INDEXED_X));
        instructions.put(0xF6, new LDInstruction("LDB", false, LDInstruction.EXTENDED));

        // ================================================
        // Instructions ST (97, A7, B7 pour A)
        // ================================================
        instructions.put(0x97, new STInstruction("STA", true, STInstruction.DIRECT));
        instructions.put(0xA7, new STInstruction("STA", true, STInstruction.INDEXED_X));
        instructions.put(0xB7, new STInstruction("STA", true, STInstruction.EXTENDED));
        
        // STB (D7, E7, F7)
        instructions.put(0xD7, new STInstruction("STB", false, STInstruction.DIRECT));
        instructions.put(0xE7, new STInstruction("STB", false, STInstruction.INDEXED_X));
        instructions.put(0xF7, new STInstruction("STB", false, STInstruction.EXTENDED));

        // ================================================
        // Instructions unaires (CLR, INC, DEC, COM, NEG, TST)
        // ================================================
        // CLR (4F, 6F, 7F)
        instructions.put(0x4F, new CLRInstruction("CLRA", CLRInstruction.DIRECT));
        instructions.put(0x6F, new CLRInstruction("CLR", CLRInstruction.DIRECT));
        instructions.put(0x7F, new CLRInstruction("CLR", CLRInstruction.EXTENDED));
        
        // INC (4C, 6C, 7C)
        instructions.put(0x4C, new INCInstruction("INCA", INCInstruction.DIRECT));
        instructions.put(0x6C, new INCInstruction("INC", INCInstruction.DIRECT));
        instructions.put(0x7C, new INCInstruction("INC", INCInstruction.EXTENDED));
        
        // DEC (4A, 6A, 7A)
        instructions.put(0x4A, new DECInstruction("DECA", DECInstruction.DIRECT));
        instructions.put(0x6A, new DECInstruction("DEC", DECInstruction.DIRECT));
        instructions.put(0x7A, new DECInstruction("DEC", DECInstruction.EXTENDED));
        
        // COM (43, 63, 73)
        instructions.put(0x43, new COMInstruction("COMA", COMInstruction.DIRECT));
        instructions.put(0x63, new COMInstruction("COM", COMInstruction.DIRECT));
        instructions.put(0x73, new COMInstruction("COM", COMInstruction.EXTENDED));
        
        // NEG (40, 60, 70)
        instructions.put(0x40, new NEGInstruction("NEGA", NEGInstruction.DIRECT));
        instructions.put(0x60, new NEGInstruction("NEG", NEGInstruction.DIRECT));
        instructions.put(0x70, new NEGInstruction("NEG", NEGInstruction.EXTENDED));
        
        // TST (4D, 6D, 7D)
        instructions.put(0x4D, new TSTInstruction("TSTA", TSTInstruction.DIRECT));
        instructions.put(0x6D, new TSTInstruction("TST", TSTInstruction.DIRECT));
        instructions.put(0x7D, new TSTInstruction("TST", TSTInstruction.EXTENDED));

        // ================================================
        // Instructions 16-bit (D, X, Y, S, U)
        // ================================================
        // LDD (CC, DC, EC, FC)
        instructions.put(0xCC, new LDDInstruction("LDD", LDDInstruction.IMM16));
        instructions.put(0xDC, new LDDInstruction("LDD", LDDInstruction.DIRECT));
        instructions.put(0xEC, new LDDInstruction("LDD", LDDInstruction.INDEXED_X));
        instructions.put(0xFC, new LDDInstruction("LDD", LDDInstruction.EXTENDED));
        
        // STD (10DD, 10ED, 10FD)
        instructions.put(0x10DD, new STDInstruction("STD", STDInstruction.DIRECT));
        instructions.put(0x10ED, new STDInstruction("STD", STDInstruction.INDEXED_X));
        instructions.put(0x10FD, new STDInstruction("STD", STDInstruction.EXTENDED));
        
        // ADDD (C3, D3, E3, F3)
        instructions.put(0xC3, new ADDDInstruction("ADDD", ADDDInstruction.IMM16));
        instructions.put(0xD3, new ADDDInstruction("ADDD", ADDDInstruction.DIRECT));
        instructions.put(0xE3, new ADDDInstruction("ADDD", ADDDInstruction.INDEXED_X));
        instructions.put(0xF3, new ADDDInstruction("ADDD", ADDDInstruction.EXTENDED));
        
        // SUBD (83, 93, A3, B3)
        instructions.put(0x83, new SUBDInstruction("SUBD", SUBDInstruction.IMM16));
        instructions.put(0x93, new SUBDInstruction("SUBD", SUBDInstruction.DIRECT));
        instructions.put(0xA3, new SUBDInstruction("SUBD", SUBDInstruction.INDEXED_X));
        instructions.put(0xB3, new SUBDInstruction("SUBD", SUBDInstruction.EXTENDED));
        
        // CMPD (1083, 1093, 10A3, 10B3)
        instructions.put(0x1083, new CMPDInstruction("CMPD", CMPDInstruction.IMM16));
        instructions.put(0x1093, new CMPDInstruction("CMPD", CMPDInstruction.DIRECT));
        instructions.put(0x10A3, new CMPDInstruction("CMPD", CMPDInstruction.INDEXED_X));
        instructions.put(0x10B3, new CMPDInstruction("CMPD", CMPDInstruction.EXTENDED));
        
        // LDX (8E, 9E, AE, BE)
        instructions.put(0x8E, new LDXInstruction("LDX", LDXInstruction.IMM16));
        instructions.put(0x9E, new LDXInstruction("LDX", LDXInstruction.DIRECT));
        instructions.put(0xAE, new LDXInstruction("LDX", LDXInstruction.INDEXED_X));
        instructions.put(0xBE, new LDXInstruction("LDX", LDXInstruction.EXTENDED));
        
        // STX (10FE, 109F, 10AF, 10BF)
        instructions.put(0x10DF, new STXInstruction("STX", STXInstruction.DIRECT));
        instructions.put(0x10EF, new STXInstruction("STX", STXInstruction.INDEXED_X));
        instructions.put(0x10FF, new STXInstruction("STX", STXInstruction.EXTENDED));
        
        // LDY (10CE, 109E, 10AE, 10BE)
        instructions.put(0x10CE, new LDYInstruction("LDY", LDYInstruction.IMM16));
        instructions.put(0x109E, new LDYInstruction("LDY", LDYInstruction.DIRECT));
        instructions.put(0x10AE, new LDYInstruction("LDY", LDYInstruction.INDEXED_X));
        instructions.put(0x10BE, new LDYInstruction("LDY", LDYInstruction.EXTENDED));
        
        // STY (10DF, 10EF, 10FF)
        instructions.put(0x10DF, new STYInstruction("STY", STYInstruction.DIRECT));
        instructions.put(0x10EF, new STYInstruction("STY", STYInstruction.INDEXED_X));
        instructions.put(0x10FF, new STYInstruction("STY", STYInstruction.EXTENDED));
        
        // LDS (10FE, 10DF, 10EF, 10FF)
        instructions.put(0x10FE, new LDSInstruction("LDS", LDSInstruction.IMM16));
        instructions.put(0x109F, new LDSInstruction("LDS", LDSInstruction.DIRECT));
        instructions.put(0x10AF, new LDSInstruction("LDS", LDSInstruction.INDEXED_X));
        instructions.put(0x10BF, new LDSInstruction("LDS", LDSInstruction.EXTENDED));
        
        // STS (10DF, 10EF, 10FF)
        instructions.put(0x10D7, new STSInstruction("STS", STSInstruction.DIRECT));
        instructions.put(0x10E7, new STSInstruction("STS", STSInstruction.INDEXED_X));
        instructions.put(0x10F7, new STSInstruction("STS", STSInstruction.EXTENDED));
        
        // LDU (CE, DE, EE, FE)
        instructions.put(0xCE, new LDUInstruction("LDU", LDUInstruction.IMM16));
        instructions.put(0xDE, new LDUInstruction("LDU", LDUInstruction.DIRECT));
        instructions.put(0xEE, new LDUInstruction("LDU", LDUInstruction.INDEXED_X));
        instructions.put(0xFE, new LDUInstruction("LDU", LDUInstruction.EXTENDED));
        
        // STU (DF, EF, FF)
        instructions.put(0xDF, new STUInstruction("STU", STUInstruction.DIRECT));
        instructions.put(0xEF, new STUInstruction("STU", STUInstruction.INDEXED_X));
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

        // ================================================
        // Instructions JMP et JSR
        // ================================================
        instructions.put(0x6E, new JMPInstruction("JMP", JMPInstruction.INDEXED_X));
        instructions.put(0x7E, new JMPInstruction("JMP", JMPInstruction.EXTENDED));
        instructions.put(0xAD, new JSRInstruction("JSR", JSRInstruction.INDEXED_X));
        instructions.put(0xBD, new JSRInstruction("JSR", JSRInstruction.EXTENDED));

        // ================================================
        // Instructions de rotation/décalage
        // ================================================
        instructions.put(0x47, new ASRInstruction("ASRA", true));
        instructions.put(0x57, new ASRInstruction("ASR", false));
        instructions.put(0x44, new LSRInstruction("LSRA", true));
        instructions.put(0x54, new LSRInstruction("LSR", false));
        instructions.put(0x49, new ROLInstruction("ROLA", true));
        instructions.put(0x59, new ROLInstruction("ROL", false));
        instructions.put(0x46, new RORInstruction("RORA", true));
        instructions.put(0x56, new RORInstruction("ROR", false));
        instructions.put(0x48, new LSLInstruction("LSLA", true));
        instructions.put(0x58, new LSLInstruction("LSL", false));
    }

    /**
     * Décode l'instruction à partir de l'opcode
     * @param opcode L'opcode de l'instruction
     * @param cpu Le processeur
     * @return L'instruction décodée, ou null si l'opcode est inconnu
     */
    public static Instruction decode(int opcode, CPU cpu) {
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
