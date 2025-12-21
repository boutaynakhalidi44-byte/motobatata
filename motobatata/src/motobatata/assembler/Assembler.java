package motobatata.assembler;

import java.util.*;
import motobatata.addressing.AddressingModeType;

/**
 * Assembleur simplifié pour Motorola 6809
 * Produit un tableau d'octets à partir d'un programme assembleur
 */
public class Assembler {

    private Map<String, Integer> labels = new HashMap<>();

    /**
     * Assemble le code source (tableau de lignes) en tableau d'octets
     */
    public byte[] assemble(String[] lines) {
        List<Byte> memory = new ArrayList<>();
        int pc = 0;

        // --- Première passe : collecte des labels ---
        for (String line : lines) {
            String cleanLine = removeSemicolonAndAfter(line).trim();
            if (cleanLine.endsWith(":")) {
                labels.put(cleanLine.substring(0, cleanLine.length() - 1).toUpperCase(), pc);
            } else if (!cleanLine.isEmpty()) {
                pc += 1; // approximation, pour calculer l'adresse des labels
            }
        }

        // --- Deuxième passe : assembler les instructions ---
        pc = 0;
        for (String line : lines) {
            // Enlever les commentaires (tout après ;)
            String cleanLine = removeSemicolonAndAfter(line).trim();
            
            // Ignorer les lignes vides, commentaires et labels
            if (cleanLine.isEmpty() || cleanLine.endsWith(":")) {
                continue;
            }
            
            // Vérifier que la ligne contient un point virgule (obligatoire pour les instructions)
            if (!line.contains(";")) {
                System.out.println("Erreur: La ligne d'instruction '" + cleanLine + "' doit se terminer par un point virgule (;)");
                continue;
            }
            
            String[] parts = cleanLine.split("\\s+", 2);
            String mnemonic = parts[0].toUpperCase();
            String operand = parts.length > 1 ? parts[1].trim() : null;

            List<Byte> instrBytes = assembleInstruction(mnemonic, operand, pc);
            memory.addAll(instrBytes);
            pc += instrBytes.size();
        }

        // Convertir en tableau d'octets
        byte[] result = new byte[memory.size()];
        for (int i = 0; i < memory.size(); i++) result[i] = memory.get(i);
        return result;
    }
    
    /**
     * Enlève le point virgule et tout ce qui suit (commentaire)
     */
    private String removeSemicolonAndAfter(String line) {
        int semicolonIndex = line.indexOf(';');
        if (semicolonIndex >= 0) {
            return line.substring(0, semicolonIndex);
        }
        return line;
    }

    /**
     * Retourne les octets correspondant à une instruction
     */
    private List<Byte> assembleInstruction(String mnemonic, String operand, int pc) {
        List<Byte> bytes = new ArrayList<>();

        // Définir les modes d'adressage
        AddressingModeType mode = null;
        if (operand != null) {
            if (operand.startsWith("#")) mode = AddressingModeType.IMMEDIATE;
            else if (operand.startsWith("<")) mode = AddressingModeType.DIRECT;
            else if (operand.contains(",")) mode = AddressingModeType.INDEXED;
            else if (labels.containsKey(operand.toUpperCase())) mode = AddressingModeType.RELATIVE;
            else mode = AddressingModeType.EXTENDED;
        }

        // Obtenir l'opcode en fonction du mnémotechnique et du mode
        Integer opcode = getOpcode(mnemonic, mode);
        if (opcode == null) {
            System.out.println("Instruction non supportée : " + mnemonic);
            return bytes;
        }

        // Gérer les préfixes (ex: LDY)
        if (opcode > 0xFF) {
            bytes.add((byte) ((opcode >> 8) & 0xFF)); // préfixe
            bytes.add((byte) (opcode & 0xFF));        // opcode
        } else {
            bytes.add(opcode.byteValue());
        }

        // Ajouter les opérandes selon le mode
        if (mode != null) {
            switch (mode) {
                case IMMEDIATE:
                    int immVal = parseValue(operand.substring(1));
                    // Si instruction avec imm16
                    if (isExtendedImmediate(mnemonic)) {
                        bytes.add((byte) (immVal >> 8));
                        bytes.add((byte) (immVal & 0xFF));
                    } else {
                        bytes.add((byte) immVal);
                    }
                    break;

                case DIRECT:
                    int dirAddr = parseValue(operand.replace("<", ""));
                    bytes.add((byte) dirAddr);
                    break;

                case EXTENDED:
                    int extAddr = parseValue(operand);
                    bytes.add((byte) (extAddr >> 8));
                    bytes.add((byte) (extAddr & 0xFF));
                    break;

                case INDEXED:
                    bytes.add((byte) parseIndexedPostbyte(operand));
                    break;

                case RELATIVE:
                    int target = labels.getOrDefault(operand.toUpperCase(), pc);
                    int offset = target - (pc + 2);
                    bytes.add((byte) offset);
                    break;
            }
        }

        return bytes;
    }

    private boolean isExtendedImmediate(String mnemonic) {
        // Instructions utilisant 16 bits immédiat
        return mnemonic.matches("LDX|LDY|LDS|LDU|CMPD");
    }

    private int parseIndexedPostbyte(String operand) {
        if (operand.contains(",X")) return 0x84;
        if (operand.contains(",Y")) return 0xA4;
        if (operand.contains(",U")) return 0xC4;
        if (operand.contains(",S")) return 0xE4;
        if (operand.contains(",PC")) return 0x8C;
        return 0x84; // défaut ,X
    }

    /**
     * Retourne l'opcode en fonction du mnémotechnique et du mode
     */
    private Integer getOpcode(String mnemonic, AddressingModeType mode) {
        // Table d'opcodes complète basée sur le Motorola 6809
        Map<String, Map<AddressingModeType, Integer>> opcodeMap = new HashMap<>();

        // ===== INSTRUCTIONS DE CHARGEMENT (LD) =====
        // LDA (86, 96, A6, B6)
        opcodeMap.put("LDA", createOpcodeMap(0x86, 0x96, 0xA6, 0xB6));
        // LDB (C6, D6, E6, F6)
        opcodeMap.put("LDB", createOpcodeMap(0xC6, 0xD6, 0xE6, 0xF6));
        
        // ===== INSTRUCTIONS DE STOCKAGE (ST) =====
        // STA (97, A7, B7)
        Map<AddressingModeType, Integer> staMap = new HashMap<>();
        staMap.put(AddressingModeType.DIRECT, 0x97);
        staMap.put(AddressingModeType.INDEXED, 0xA7);
        staMap.put(AddressingModeType.EXTENDED, 0xB7);
        opcodeMap.put("STA", staMap);
        
        // STB (D7, E7, F7)
        Map<AddressingModeType, Integer> stbMap = new HashMap<>();
        stbMap.put(AddressingModeType.DIRECT, 0xD7);
        stbMap.put(AddressingModeType.INDEXED, 0xE7);
        stbMap.put(AddressingModeType.EXTENDED, 0xF7);
        opcodeMap.put("STB", stbMap);
        
        // ===== INSTRUCTIONS 16-bit D (LDD, STD, ADDD, SUBD, CMPD) =====
        Map<AddressingModeType, Integer> lddMap = new HashMap<>();
        lddMap.put(AddressingModeType.IMMEDIATE, 0xCC);
        lddMap.put(AddressingModeType.DIRECT, 0xDC);
        lddMap.put(AddressingModeType.INDEXED, 0xEC);
        lddMap.put(AddressingModeType.EXTENDED, 0xFC);
        opcodeMap.put("LDD", lddMap);
        
        Map<AddressingModeType, Integer> stdMap = new HashMap<>();
        stdMap.put(AddressingModeType.DIRECT, 0x10DD);
        stdMap.put(AddressingModeType.INDEXED, 0x10ED);
        stdMap.put(AddressingModeType.EXTENDED, 0x10FD);
        opcodeMap.put("STD", stdMap);
        
        Map<AddressingModeType, Integer> adddMap = new HashMap<>();
        adddMap.put(AddressingModeType.IMMEDIATE, 0xC3);
        adddMap.put(AddressingModeType.DIRECT, 0xD3);
        adddMap.put(AddressingModeType.INDEXED, 0xE3);
        adddMap.put(AddressingModeType.EXTENDED, 0xF3);
        opcodeMap.put("ADDD", adddMap);
        
        Map<AddressingModeType, Integer> subdMap = new HashMap<>();
        subdMap.put(AddressingModeType.IMMEDIATE, 0x83);
        subdMap.put(AddressingModeType.DIRECT, 0x93);
        subdMap.put(AddressingModeType.INDEXED, 0xA3);
        subdMap.put(AddressingModeType.EXTENDED, 0xB3);
        opcodeMap.put("SUBD", subdMap);
        
        Map<AddressingModeType, Integer> cmpdMap = new HashMap<>();
        cmpdMap.put(AddressingModeType.IMMEDIATE, 0x1083);
        cmpdMap.put(AddressingModeType.DIRECT, 0x1093);
        cmpdMap.put(AddressingModeType.INDEXED, 0x10A3);
        cmpdMap.put(AddressingModeType.EXTENDED, 0x10B3);
        opcodeMap.put("CMPD", cmpdMap);

        // ===== INSTRUCTIONS D'ADDITION (ADD) =====
        // ADDA (8B, 9B, AB, BB)
        opcodeMap.put("ADDA", createOpcodeMap(0x8B, 0x9B, 0xAB, 0xBB));
        // ADDB (CB, DB, EB, FB)
        opcodeMap.put("ADDB", createOpcodeMap(0xCB, 0xDB, 0xEB, 0xFB));

        // ===== INSTRUCTIONS DE SOUSTRACTION (SUB) =====
        // SUBA (80, 90, A0, B0)
        opcodeMap.put("SUBA", createOpcodeMap(0x80, 0x90, 0xA0, 0xB0));
        // SUBB (C0, D0, E0, F0)
        opcodeMap.put("SUBB", createOpcodeMap(0xC0, 0xD0, 0xE0, 0xF0));

        // ===== INSTRUCTIONS ADC (ADD with Carry) =====
        // ADCA (89, 99, A9, B9)
        opcodeMap.put("ADCA", createOpcodeMap(0x89, 0x99, 0xA9, 0xB9));
        // ADCB (C9, D9, E9, F9)
        opcodeMap.put("ADCB", createOpcodeMap(0xC9, 0xD9, 0xE9, 0xF9));

        // ===== INSTRUCTIONS SBC (SUB with Carry) =====
        // SBCA (82, 92, A2, B2)
        opcodeMap.put("SBCA", createOpcodeMap(0x82, 0x92, 0xA2, 0xB2));
        // SBCB (C2, D2, E2, F2)
        opcodeMap.put("SBCB", createOpcodeMap(0xC2, 0xD2, 0xE2, 0xF2));

        // ===== INSTRUCTIONS ET LOGIQUE (AND) =====
        // ANDA (84, 94, A4, B4)
        opcodeMap.put("ANDA", createOpcodeMap(0x84, 0x94, 0xA4, 0xB4));
        // ANDB (C4, D4, E4, F4)
        opcodeMap.put("ANDB", createOpcodeMap(0xC4, 0xD4, 0xE4, 0xF4));

        // ===== INSTRUCTIONS OU LOGIQUE (OR) =====
        // ORA (8A, 9A, AA, BA)
        opcodeMap.put("ORA", createOpcodeMap(0x8A, 0x9A, 0xAA, 0xBA));
        // ORB (CA, DA, EA, FA)
        opcodeMap.put("ORB", createOpcodeMap(0xCA, 0xDA, 0xEA, 0xFA));

        // ===== INSTRUCTIONS OU EXCLUSIF (EOR) =====
        // EORA (88, 98, A8, B8)
        opcodeMap.put("EORA", createOpcodeMap(0x88, 0x98, 0xA8, 0xB8));
        // EORB (C8, D8, E8, F8)
        opcodeMap.put("EORB", createOpcodeMap(0xC8, 0xD8, 0xE8, 0xF8));

        // ===== INSTRUCTIONS DE COMPARAISON (CMP) =====
        // CMPA (81, 91, A1, B1)
        opcodeMap.put("CMPA", createOpcodeMap(0x81, 0x91, 0xA1, 0xB1));
        // CMPB (C1, D1, E1, F1)
        opcodeMap.put("CMPB", createOpcodeMap(0xC1, 0xD1, 0xE1, 0xF1));

        // ===== INSTRUCTIONS TEST DE BIT (BIT) =====
        // BITA (85, 95, A5, B5)
        opcodeMap.put("BITA", createOpcodeMap(0x85, 0x95, 0xA5, 0xB5));
        // BITB (C5, D5, E5, F5)
        opcodeMap.put("BITB", createOpcodeMap(0xC5, 0xD5, 0xE5, 0xF5));

        // ===== INSTRUCTIONS UNAIRES =====
        // CLR
        Map<String, Integer> noOperandMap = new HashMap<>();
        noOperandMap.put("CLRA", 0x4F);
        noOperandMap.put("CLRB", 0x5F);
        
        // INC
        noOperandMap.put("INCA", 0x4C);
        noOperandMap.put("INCB", 0x5C);
        
        // DEC
        noOperandMap.put("DECA", 0x4A);
        noOperandMap.put("DECB", 0x5B);
        
        // COM
        noOperandMap.put("COMA", 0x43);
        noOperandMap.put("COMB", 0x53);
        
        // NEG
        noOperandMap.put("NEGA", 0x40);
        noOperandMap.put("NEGB", 0x50);
        
        // TST
        noOperandMap.put("TSTA", 0x4D);
        noOperandMap.put("TSTB", 0x5D);
        
        // ===== INSTRUCTIONS DE ROTATION =====
        noOperandMap.put("ASRA", 0x47);
        noOperandMap.put("ASRB", 0x57);
        noOperandMap.put("LSRA", 0x44);
        noOperandMap.put("LSRB", 0x54);
        noOperandMap.put("ROLA", 0x49);
        noOperandMap.put("ROLB", 0x59);
        noOperandMap.put("RORA", 0x46);
        noOperandMap.put("RORB", 0x56);
        noOperandMap.put("LSLA", 0x48);
        noOperandMap.put("LSLB", 0x58);

        // ===== BRANCHEMENTS RELATIFS =====
        Map<String, Integer> branchMap = new HashMap<>();
        branchMap.put("BRA", 0x20);
        branchMap.put("BRN", 0x21);
        branchMap.put("BHI", 0x22);
        branchMap.put("BLS", 0x23);
        branchMap.put("BCC", 0x24);
        branchMap.put("BCS", 0x25);
        branchMap.put("BNE", 0x26);
        branchMap.put("BEQ", 0x27);
        branchMap.put("BVC", 0x28);
        branchMap.put("BVS", 0x29);
        branchMap.put("BPL", 0x2A);
        branchMap.put("BMI", 0x2B);
        branchMap.put("BGE", 0x2C);
        branchMap.put("BLT", 0x2D);
        branchMap.put("BGT", 0x2E);
        branchMap.put("BLE", 0x2F);
        branchMap.put("BSR", 0x8D);

        // ===== INSTRUCTIONS SANS OPÉRANDE =====
        Map<String, Integer> inherentMap = new HashMap<>();
        inherentMap.put("NOP", 0x12);
        inherentMap.put("RTS", 0x39);
        inherentMap.put("RTI", 0x3B);
        inherentMap.put("SWI", 0x3F);
        inherentMap.put("ABX", 0x3A);
        inherentMap.put("MUL", 0x3D);
        inherentMap.put("DAA", 0x19);

        // ===== SAUTS =====
        // JMP et JSR
        Map<AddressingModeType, Integer> jmpMap = new HashMap<>();
        jmpMap.put(AddressingModeType.INDEXED, 0x6E);
        jmpMap.put(AddressingModeType.EXTENDED, 0x7E);
        opcodeMap.put("JMP", jmpMap);
        
        Map<AddressingModeType, Integer> jsrMap = new HashMap<>();
        jsrMap.put(AddressingModeType.INDEXED, 0xAD);
        jsrMap.put(AddressingModeType.EXTENDED, 0xBD);
        opcodeMap.put("JSR", jsrMap);

        // ===== LDX, LDY, LDS, LDU (16-bit) =====
        Map<AddressingModeType, Integer> ldxMap = new HashMap<>();
        ldxMap.put(AddressingModeType.IMMEDIATE, 0x8E);
        ldxMap.put(AddressingModeType.DIRECT, 0x9E);
        ldxMap.put(AddressingModeType.INDEXED, 0xAE);
        ldxMap.put(AddressingModeType.EXTENDED, 0xBE);
        opcodeMap.put("LDX", ldxMap);
        
        Map<AddressingModeType, Integer> ldyMap = new HashMap<>();
        ldyMap.put(AddressingModeType.IMMEDIATE, 0x10CE);
        ldyMap.put(AddressingModeType.DIRECT, 0x109E);
        ldyMap.put(AddressingModeType.INDEXED, 0x10AE);
        ldyMap.put(AddressingModeType.EXTENDED, 0x10BE);
        opcodeMap.put("LDY", ldyMap);
        
        Map<AddressingModeType, Integer> ldsMap = new HashMap<>();
        ldsMap.put(AddressingModeType.IMMEDIATE, 0x10FE);
        ldsMap.put(AddressingModeType.DIRECT, 0x109F);
        ldsMap.put(AddressingModeType.INDEXED, 0x10AF);
        ldsMap.put(AddressingModeType.EXTENDED, 0x10BF);
        opcodeMap.put("LDS", ldsMap);
        
        Map<AddressingModeType, Integer> lduMap = new HashMap<>();
        lduMap.put(AddressingModeType.IMMEDIATE, 0xCE);
        lduMap.put(AddressingModeType.DIRECT, 0xDE);
        lduMap.put(AddressingModeType.INDEXED, 0xEE);
        lduMap.put(AddressingModeType.EXTENDED, 0xFE);
        opcodeMap.put("LDU", lduMap);
        
        // ===== STX, STY, STS, STU (16-bit store) =====
        Map<AddressingModeType, Integer> stxMap = new HashMap<>();
        stxMap.put(AddressingModeType.DIRECT, 0x10DF);
        stxMap.put(AddressingModeType.INDEXED, 0x10EF);
        stxMap.put(AddressingModeType.EXTENDED, 0x10FF);
        opcodeMap.put("STX", stxMap);
        
        Map<AddressingModeType, Integer> styMap = new HashMap<>();
        styMap.put(AddressingModeType.DIRECT, 0x10DF);
        styMap.put(AddressingModeType.INDEXED, 0x10EF);
        styMap.put(AddressingModeType.EXTENDED, 0x10FF);
        opcodeMap.put("STY", styMap);
        
        Map<AddressingModeType, Integer> stsMap = new HashMap<>();
        stsMap.put(AddressingModeType.DIRECT, 0x10D7);
        stsMap.put(AddressingModeType.INDEXED, 0x10E7);
        stsMap.put(AddressingModeType.EXTENDED, 0x10F7);
        opcodeMap.put("STS", stsMap);
        
        Map<AddressingModeType, Integer> stuMap = new HashMap<>();
        stuMap.put(AddressingModeType.DIRECT, 0xDF);
        stuMap.put(AddressingModeType.INDEXED, 0xEF);
        stuMap.put(AddressingModeType.EXTENDED, 0xFF);
        opcodeMap.put("STU", stuMap);

        // ===== RÉSOLUTION DE L'OPCODE =====
        if (mode == null) {
            Integer opcode = noOperandMap.get(mnemonic);
            if (opcode != null) return opcode;
            
            opcode = inherentMap.get(mnemonic);
            if (opcode != null) return opcode;
        } else if (mode == AddressingModeType.RELATIVE) {
            return branchMap.get(mnemonic);
        } else {
            Map<AddressingModeType, Integer> map = opcodeMap.get(mnemonic);
            if (map != null) return map.get(mode);
        }

        return null;
    }

    /**
     * Crée une map d'opcodes standard (IMM8, DIRECT, INDEXED, EXTENDED)
     */
    private Map<AddressingModeType, Integer> createOpcodeMap(int imm, int dir, int idx, int ext) {
        Map<AddressingModeType, Integer> map = new HashMap<>();
        map.put(AddressingModeType.IMMEDIATE, imm);
        map.put(AddressingModeType.DIRECT, dir);
        map.put(AddressingModeType.INDEXED, idx);
        map.put(AddressingModeType.EXTENDED, ext);
        return map;
    }

    /**
     * Parse une valeur numérique en tenant compte du symbole $
     * - $XXXX = hexadécimal (avec le $)
     * - 0xXXXX = hexadécimal (avec le 0x)
     * - XXXX = décimal (sans $ ni 0x)
     */
    private int parseValue(String value) {
        value = value.trim().toUpperCase();
        
        // Hexadécimal avec $
        if (value.startsWith("$")) {
            String hexPart = value.substring(1);
            try {
                return Integer.parseInt(hexPart, 16);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid hexadecimal format: " + value);
            }
        }
        
        // Hexadécimal avec 0x
        if (value.startsWith("0X")) {
            String hexPart = value.substring(2);
            try {
                return Integer.parseInt(hexPart, 16);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid hexadecimal format: " + value);
            }
        }
        
        // Décimal (pas de $ ni 0x)
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid decimal format: " + value);
        }
    }

    /**
     * Parse une valeur hexadécimale de manière robuste
     * Accepte les formats : 0x1234, $1234, 1234
     * @deprecated Utilisez parseValue() à la place
     */
    @Deprecated
    private int parseHexValue(String value) {
        return parseValue(value);
    }
}

