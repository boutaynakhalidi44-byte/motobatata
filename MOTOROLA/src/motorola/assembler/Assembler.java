package motorola.assembler;

import java.util.*;
import motorola.addressing.AddressingModeType;

/**
 * Assembleur simplifié pour Motorola 6809
 * Produit un tableau d'octets à partir d'un programme assembleur
 */
public class Assembler {

    private final Map<String, Integer> labels = new HashMap<>();

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
            
            // Si pas d'operand et le mnemonic contient des caractères non alphabétiques, 
            // c'est peut-être du code sans espace comme "LDB[$0009]"
            if (operand == null && mnemonic.length() > 3 && !mnemonic.matches("[A-Z]+")) {
                // Extraire le mnemonic (lettres seulement) et le reste comme operand
                int i = 0;
                while (i < mnemonic.length() && Character.isLetter(mnemonic.charAt(i))) {
                    i++;
                }
                if (i > 0 && i < mnemonic.length()) {
                    operand = mnemonic.substring(i);
                    mnemonic = mnemonic.substring(0, i);
                    //System.out.println("DEBUG Assembler: Parsed no-space format: mnemonic=" + mnemonic + " operand=" + operand);
                }
            }

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

        // Traitement spécial pour EXG et TFR (instructions avec postbyte)
        if ((mnemonic.equals("EXG") || mnemonic.equals("TFR")) && operand != null) {
            int opcode = mnemonic.equals("EXG") ? 0x1E : 0x1F;
            bytes.add((byte) opcode);
            
            // Parser le format: "REG1,REG2"
            String[] regParts = operand.split(",");
            if (regParts.length == 2) {
                String reg1 = regParts[0].trim().toUpperCase();
                String reg2 = regParts[1].trim().toUpperCase();
                
                int reg1Code = getRegisterCode(reg1);
                int reg2Code = getRegisterCode(reg2);
                
                if (reg1Code >= 0 && reg2Code >= 0) {
                    // Postbyte: bits 7-4 = reg1, bits 3-0 = reg2
                    byte postbyte = (byte) ((reg1Code << 4) | reg2Code);
                    bytes.add(postbyte);
                    return bytes;
                } else {
                    System.out.println("Registre inconnu dans " + mnemonic + " : " + operand);
                    return bytes;
                }
            } else {
                System.out.println("Format invalide pour " + mnemonic + " : " + operand);
                return bytes;
            }
        }

        // Définir les modes d'adressage
        AddressingModeType mode = null;
        if (operand != null) {
            if (operand.startsWith("#")) mode = AddressingModeType.IMMEDIATE;
            else if (operand.startsWith("[") && operand.contains(",")) {
                // Indirect indexed: [,X] ou [$20,X] ou [$2000,X], etc.
                //System.out.println("DEBUG Assembler: Recognized '[...,...' as INDIRECT_INDEXED");
                mode = AddressingModeType.INDIRECT_INDEXED;
            }
            else if (operand.startsWith("[")) {
                //System.out.println("DEBUG Assembler: Recognized '[' as EXTENDED_INDIRECT");
                mode = AddressingModeType.EXTENDED_INDIRECT;
            }
            else if (operand.contains(",")) mode = AddressingModeType.INDEXED;  // Vérifier INDEXED EN PREMIER
            else if (operand.startsWith("<")) mode = AddressingModeType.DIRECT;
            else if (operand.startsWith(">")) mode = AddressingModeType.EXTENDED;
            else if (operand.startsWith("$")) {
                // Déterminer DIRECT vs EXTENDED basé sur le nombre de chiffres hex
                String hexPart = operand.substring(1).toUpperCase();
                int value = parseValue(operand);
                // DIRECT = 0x00-0xFF (1-2 chiffres hex), EXTENDED = 0x100-0xFFFF (3-4 chiffres hex)
                if (value <= 0xFF && hexPart.length() <= 2) {
                    mode = AddressingModeType.DIRECT;
                } else {
                    mode = AddressingModeType.EXTENDED;
                }
            }
            else if (labels.containsKey(operand.toUpperCase())) mode = AddressingModeType.RELATIVE;
            else mode = AddressingModeType.DIRECT;  // Mode par défaut = DIRECT (avec DP)
        }
        //System.out.println("DEBUG Assembler: operand='" + operand + "' -> mode=" + mode);

        // Obtenir l'opcode en fonction du mnémotechnique et du mode
        Integer opcode = getOpcode(mnemonic, mode);
        if (opcode == null) {
            System.out.println("Instruction non supportée : " + mnemonic + " avec mode " + mode);
            return bytes;
        }

        //System.out.println("DEBUG Assembler: mnemonic=" + mnemonic + " operand=" + operand + " mode=" + mode + " opcode=0x" + String.format("%02X", opcode));

        // Pour EXTENDED_INDIRECT, ajouter le marqueur 0x04 en premier
        if (mode == AddressingModeType.EXTENDED_INDIRECT) {
            //System.out.println("DEBUG Assembler: Adding EXTENDED_INDIRECT marker 0x04");
            bytes.add((byte) 0x04);
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
                    // Mode DIRECT : récupère l'offset 8-bit (0-255)
                    // La vraie adresse sera (DP << 8) | offset au runtime
                    String dirOperand = operand.replace("<", "").trim();  // Enlever le < s'il existe
                    int dirAddr = parseValue(dirOperand) & 0xFF;  // Garder que 8 bits!
                    bytes.add((byte) dirAddr);
                    break;

                case EXTENDED:
                    int extAddr = parseValue(operand.replace(">", ""));
                    bytes.add((byte) (extAddr >> 8));
                    bytes.add((byte) (extAddr & 0xFF));
                    break;

                case EXTENDED_INDIRECT:
                    // Format: [$2000] ou [> $2000]
                    // Le marqueur 0x04 a déjà été ajouté plus haut
                    // Ajouter seulement les données 16-bit
                    String indirectOperand = operand.substring(1, operand.length() - 1).trim();
                    indirectOperand = indirectOperand.replace(">", "").trim();
                    int indirectAddr = parseValue(indirectOperand);
                    bytes.add((byte) (indirectAddr >> 8));
                    bytes.add((byte) (indirectAddr & 0xFF));
                    break;

                case INDEXED:
                    // Extraire l'offset et le registre indexé
                    String[] indexParts = operand.split(",");
                    String offsetStr = indexParts[0].trim().toUpperCase();
                    String registerPart = indexParts[1].trim().toUpperCase();
                    
                    // Vérifier si c'est un offset accumulateur (A, B, ou D)
                    if (offsetStr.equals("A") || offsetStr.equals("B") || offsetStr.equals("D")) {
                        // Mode accumulateur offset: A,X / B,Y / D,X, etc.
                        byte acPostbyte = generateAccumulatorIndexedPostbyte(offsetStr, registerPart);
                        bytes.add(acPostbyte);
                    } else if (offsetStr.isEmpty()) {
                        // Mode indexé SANS offset: ,X / ,Y, etc.
                        // C'est le mode "indexed with zero offset"
                        byte postbyte = generateIndexedPostbyte(registerPart, 0);
                        bytes.add(postbyte);
                    } else {
                        // Mode offset normal: $20,X / 30,X, etc.
                        // NE PAS enlever le $ devant, parseValue() le reconnait
                        int indexedOffset = parseValue(offsetStr);
                        //System.out.println("DEBUG Assembler.generateIndexedPostbyte: offsetStr='" + offsetStr + "' -> indexedOffset=" + indexedOffset);
                        
                        // Générer le postbyte correct pour le 6809
                        byte postbyte = generateIndexedPostbyte(registerPart, indexedOffset);
                        //System.out.println("DEBUG Assembler.generateIndexedPostbyte: registerPart='" + registerPart + "', indexedOffset=" + indexedOffset + " -> postbyte=0x" + String.format("%02X", postbyte & 0xFF));
                        bytes.add(postbyte);
                        
                        // Si l'offset est > 5 bits, ajouter l'octet(s) d'offset supplémentaire(s)
                        if (indexedOffset > 15 || indexedOffset < -16) {
                            if (indexedOffset > 255 || indexedOffset < -256) {
                                // 16-bit offset
                                bytes.add((byte) (indexedOffset >> 8));
                                bytes.add((byte) indexedOffset);
                            } else {
                                // 8-bit offset
                                bytes.add((byte) indexedOffset);
                            }
                        }
                    }
                    break;

                case INDIRECT_INDEXED:
                    // Format: [,X] ou [$20,X] ou [$2000,X] ou [A,X], etc.
                    // Enlever les crochets
                    String indexedContent = operand.substring(1, operand.length() - 1).trim();
                    
                    String[] indParts = indexedContent.split(",");
                    String indOffsetStr = indParts[0].trim().toUpperCase();
                    String indRegisterPart = indParts[1].trim().toUpperCase();
                    
                    // Vérifier si c'est offset accumulateur ou offset normal
                    if (indOffsetStr.isEmpty()) {
                        // Mode [,X] sans offset
                        byte indPostbyte = generateIndirectIndexedPostbyte(indRegisterPart, 0, true);
                        bytes.add(indPostbyte);
                    } else if (indOffsetStr.equals("A") || indOffsetStr.equals("B") || indOffsetStr.equals("D")) {
                        // Mode [A,X] ou [B,X] ou [D,X]
                        byte indPostbyte = generateIndirectAccumulatorIndexedPostbyte(indOffsetStr, indRegisterPart);
                        bytes.add(indPostbyte);
                    } else {
                        // Mode [$20,X] ou [$2000,X]
                        if (indOffsetStr.startsWith("$") || indOffsetStr.startsWith("<")) {
                            indOffsetStr = indOffsetStr.substring(1);
                        }
                        int indOffset = parseValue(indOffsetStr);
                        byte indPostbyte = generateIndirectIndexedPostbyte(indRegisterPart, indOffset, false);
                        bytes.add(indPostbyte);
                        
                        // Ajouter l'offset si nécessaire
                        if (indOffset > 15 || indOffset < -16) {
                            if (indOffset > 255 || indOffset < -256) {
                                bytes.add((byte) (indOffset >> 8));
                                bytes.add((byte) indOffset);
                            } else {
                                bytes.add((byte) indOffset);
                            }
                        }
                    }
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
        return mnemonic.matches("LDX|LDY|LDS|LDU|CMPD|ADDD|SUBD|LDD");
    }

    /**
     * Génère le postbyte pour accumulator offset indirect indexing ([A,X], [B,Y], etc.)
     */
    private byte generateIndirectAccumulatorIndexedPostbyte(String accum, String registerPart) {
        int accumBit;
        if (accum.equals("A")) {
            accumBit = 0x06;
        } else if (accum.equals("B")) {
            accumBit = 0x05;
        } else if (accum.equals("D")) {
            accumBit = 0x0B;  // D (16-bit accumulator offset)
        } else {
            accumBit = 0x06;  // défaut A
        }
        
        // Use the same RR/E mapping as regular indexed postbytes but with E=1
        if (registerPart.equals("X")) return (byte) (0x10 | accumBit);
        else if (registerPart.equals("Y")) return (byte) (0x50 | accumBit);
        else if (registerPart.equals("U")) return (byte) (0x90 | accumBit);
        else if (registerPart.equals("S")) return (byte) (0xD0 | accumBit);
        else if (registerPart.equals("PC")) return (byte) (0x90 | accumBit | 0x08);

        return (byte) (0x10 | accumBit);
    }
    
    /**
     * Génère le postbyte pour indirect indexed: [,X] ou [$20,X] ou [$2000,X], etc.
     */
    private byte generateIndirectIndexedPostbyte(String registerPart, int offset, boolean noOffset) {
        int regBits = 0;
        String reg = registerPart.replaceAll("[+\\-]", "").toUpperCase();
        
        // RR bits in positions 7-6: X=00, Y=01, U=10, S=11. Keep consistent with generateIndexedPostbyte
        if (reg.equals("X")) regBits = 0x00;
        else if (reg.equals("Y")) regBits = 0x40;
        else if (reg.equals("U")) regBits = 0x80;
        else if (reg.equals("S")) regBits = 0xC0;
        else if (reg.equals("PC")) regBits = 0x80 | 0x08;
        
        // Vérifier les modificateurs
        if (registerPart.contains("++")) {
            return (byte) (regBits | 0x01);
        } else if (registerPart.endsWith("+")) {
            return (byte) (regBits | 0x00);
        } else if (registerPart.startsWith("--")) {
            return (byte) (regBits | 0x0E);
        } else if (registerPart.startsWith("-")) {
            return (byte) (regBits | 0x0F);
        } else if (noOffset) {
            // [,X] sans offset -> set E bit (0x10) and mode 0x04
            return (byte) (regBits | 0x10 | 0x04);
        } else if (offset >= -16 && offset <= 15) {
            // 5-bit offset
            int offsetBits = offset & 0x1F;
            return (byte) (regBits | offsetBits);
        } else if (offset >= -128 && offset <= 127) {
            // 8-bit offset
            return (byte) (regBits | 0x08);
        } else {
            // 16-bit offset
            return (byte) (regBits | 0x09);
        }
    }

    /**
     * Génère le postbyte pour accumulator offset indexing (A,X / B,Y, etc.)
     * A,X=0x86, B,X=0x85, A,Y=0xA6, B,Y=0xA5, etc.
     */
    private byte generateAccumulatorIndexedPostbyte(String accum, String registerPart) {
        int accumBit;
        if (accum.equals("A")) {
            accumBit = 0x06;  // 0110 pour A
        } else if (accum.equals("B")) {
            accumBit = 0x05;  // 0101 pour B
        } else if (accum.equals("D")) {
            accumBit = 0x0B;  // 1011 pour D (16-bit accumulateur offset)
        } else {
            accumBit = 0x06;  // défaut A
        }
        
        // Accumulator offset requires E bit (bit 4 = 1, i.e. 0x10)
        if (registerPart.equals("X")) return (byte) (0x10 | accumBit);  // RR=00 for X, E=1
        else if (registerPart.equals("Y")) return (byte) (0x50 | accumBit);  // RR=01 for Y, E=1
        else if (registerPart.equals("U")) return (byte) (0x90 | accumBit);  // RR=10 for U, E=1
        else if (registerPart.equals("S")) return (byte) (0xD0 | accumBit);  // RR=11 for S, E=1
        else if (registerPart.equals("PC")) return (byte) (0x90 | accumBit + 0x08);
        
        return (byte) (0x10 | accumBit); // défaut ,X with E=1
    }

    /**
     * Génère le postbyte correct pour le mode INDEXED du 6809
     * Supporte: ,R / ,R+ / ,R++ / ,-R / ,--R / offset,R (5-bit, 8-bit, 16-bit)
     */
    private byte generateIndexedPostbyte(String registerPart, int offset) {
        int regBits = 0;
        
        // Extraire le registre et le modificateur
        String reg = registerPart.replaceAll("[+\\-]", "").toUpperCase();
        
        // Les bits de registre DOIVENT être sur les bits 7-6 (RR E BBBBB)
        if (reg.equals("X")) regBits = 0x00;  // RR = 00
        else if (reg.equals("Y")) regBits = 0x40;  // RR = 01 (sur bits 7-6)
        else if (reg.equals("U")) regBits = 0x80;  // RR = 10
        else if (reg.equals("S")) regBits = 0xC0;  // RR = 11
        else if (reg.equals("PC")) regBits = 0x80; // PC: RR = 10
        
        // Vérifier les modificateurs
        if (registerPart.contains("++")) {
            // Post-increment twice: RR 1 00001 où bit 4=1 (E bit)
            return (byte) (regBits | 0x11);
        } else if (registerPart.endsWith("+")) {
            // Post-increment: RR 1 00000 où bit 4=1 (E bit)
            return (byte) (regBits | 0x10);
        } else if (registerPart.startsWith("--")) {
            // Pre-decrement twice: RR 1 11110 où bit 4=1 (E bit)
            return (byte) (regBits | 0x1E);
        } else if (registerPart.startsWith("-")) {
            // Pre-decrement: RR 1 11111 où bit 4=1 (E bit)
            return (byte) (regBits | 0x1F);
        } else if (offset >= -8 && offset <= 7) {
            // Offset 4-bit: RR 0 OOOO (E=0, bits 3-0 = signed offset, bit 3 = sign)
            // Range: -8 to +7
            int offsetBits = offset & 0x0F;  // Only 4 bits
            return (byte) (regBits | offsetBits);
        } else if (offset >= -128 && offset <= 127) {
            // Offset 8-bit: RR 1 1000 (E=1, bits 3-0=1000, next byte is offset)
            return (byte) (regBits | 0x18);
        } else {
            // Offset 16-bit: RR 1 1001 (E=1, bits 3-0=1001, next 2 bytes are offset)
            return (byte) (regBits | 0x19);
        }
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
        staMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xB7);
        opcodeMap.put("STA", staMap);
        
        // STB (D7, E7, F7)
        Map<AddressingModeType, Integer> stbMap = new HashMap<>();
        stbMap.put(AddressingModeType.DIRECT, 0xD7);
        stbMap.put(AddressingModeType.INDEXED, 0xE7);
        stbMap.put(AddressingModeType.EXTENDED, 0xF7);
        stbMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xF7);
        opcodeMap.put("STB", stbMap);
        
        // ===== INSTRUCTIONS 16-bit D (LDD, STD, ADDD, SUBD, CMPD) =====
        Map<AddressingModeType, Integer> lddMap = new HashMap<>();
        lddMap.put(AddressingModeType.IMMEDIATE, 0xCC);
        lddMap.put(AddressingModeType.DIRECT, 0xDC);
        lddMap.put(AddressingModeType.INDEXED, 0xEC);
        lddMap.put(AddressingModeType.EXTENDED, 0xFC);
        lddMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xFC);
        opcodeMap.put("LDD", lddMap);
        
        Map<AddressingModeType, Integer> stdMap = new HashMap<>();
        stdMap.put(AddressingModeType.DIRECT, 0x10DD);
        stdMap.put(AddressingModeType.INDEXED, 0x10ED);
        stdMap.put(AddressingModeType.EXTENDED, 0x10FD);
        stdMap.put(AddressingModeType.EXTENDED_INDIRECT, 0x10FD);
        opcodeMap.put("STD", stdMap);
        
        Map<AddressingModeType, Integer> adddMap = new HashMap<>();
        adddMap.put(AddressingModeType.IMMEDIATE, 0xC3);
        adddMap.put(AddressingModeType.DIRECT, 0xD3);
        adddMap.put(AddressingModeType.INDEXED, 0xE3);
        adddMap.put(AddressingModeType.EXTENDED, 0xF3);
        adddMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xF3);
        opcodeMap.put("ADDD", adddMap);
        
        Map<AddressingModeType, Integer> subdMap = new HashMap<>();
        subdMap.put(AddressingModeType.IMMEDIATE, 0x83);
        subdMap.put(AddressingModeType.DIRECT, 0x93);
        subdMap.put(AddressingModeType.INDEXED, 0xA3);
        subdMap.put(AddressingModeType.EXTENDED, 0xB3);
        subdMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xB3);
        opcodeMap.put("SUBD", subdMap);
        
        Map<AddressingModeType, Integer> cmpdMap = new HashMap<>();
        cmpdMap.put(AddressingModeType.IMMEDIATE, 0x1083);
        cmpdMap.put(AddressingModeType.DIRECT, 0x1093);
        cmpdMap.put(AddressingModeType.INDEXED, 0x10A3);
        cmpdMap.put(AddressingModeType.EXTENDED, 0x10B3);
        cmpdMap.put(AddressingModeType.EXTENDED_INDIRECT, 0x10B3);
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

        // ===== INSTRUCTIONS DE MODIFICATION DE MÉMOIRE (INC, DEC, CLR, COM, NEG, TST) =====
        // INC (0C DIRECT, 6C INDEXED, 7C EXTENDED)
        Map<AddressingModeType, Integer> incMap = new HashMap<>();
        incMap.put(AddressingModeType.DIRECT, 0x0C);
        incMap.put(AddressingModeType.INDEXED, 0x6C);
        incMap.put(AddressingModeType.EXTENDED, 0x7C);
        opcodeMap.put("INC", incMap);
        
        // DEC (0A DIRECT, 6A INDEXED, 7A EXTENDED)
        Map<AddressingModeType, Integer> decMap = new HashMap<>();
        decMap.put(AddressingModeType.DIRECT, 0x0A);
        decMap.put(AddressingModeType.INDEXED, 0x6A);
        decMap.put(AddressingModeType.EXTENDED, 0x7A);
        opcodeMap.put("DEC", decMap);
        
        // CLR (0F DIRECT, 6F INDEXED, 7F EXTENDED)
        Map<AddressingModeType, Integer> clrMap = new HashMap<>();
        clrMap.put(AddressingModeType.DIRECT, 0x0F);
        clrMap.put(AddressingModeType.INDEXED, 0x6F);
        clrMap.put(AddressingModeType.EXTENDED, 0x7F);
        opcodeMap.put("CLR", clrMap);
        
    // COM removed
        
        // NEG (00 DIRECT, 60 INDEXED, 70 EXTENDED)
        Map<AddressingModeType, Integer> negMap = new HashMap<>();
        negMap.put(AddressingModeType.DIRECT, 0x00);
        negMap.put(AddressingModeType.INDEXED, 0x60);
        negMap.put(AddressingModeType.EXTENDED, 0x70);
        opcodeMap.put("NEG", negMap);
        
        // TST (0D DIRECT, 6D INDEXED, 7D EXTENDED)
        Map<AddressingModeType, Integer> tstMap = new HashMap<>();
        tstMap.put(AddressingModeType.DIRECT, 0x0D);
        tstMap.put(AddressingModeType.INDEXED, 0x6D);
        tstMap.put(AddressingModeType.EXTENDED, 0x7D);
        opcodeMap.put("TST", tstMap);
        
    // Rotation/shift instructions ASR/LSR/ROL removed
        
        // ROR (06 DIRECT, 66 INDEXED, 76 EXTENDED)
        Map<AddressingModeType, Integer> rorMap = new HashMap<>();
        rorMap.put(AddressingModeType.DIRECT, 0x06);
        rorMap.put(AddressingModeType.INDEXED, 0x66);
        rorMap.put(AddressingModeType.EXTENDED, 0x76);
        opcodeMap.put("ROR", rorMap);
        
    // LSL removed

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
        
    // COM removed
        
        // NEG
        noOperandMap.put("NEGA", 0x40);
        noOperandMap.put("NEGB", 0x50);
        
        // TST
        noOperandMap.put("TSTA", 0x4D);
        noOperandMap.put("TSTB", 0x5D);
        
    // Rotation entries ASR/LSR/ROL/LSL removed; ROR kept
    noOperandMap.put("RORA", 0x46);
    noOperandMap.put("RORB", 0x56);

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
    // RTS/RTI removed
        inherentMap.put("HALT", 0x3F);
        inherentMap.put("ABX", 0x3A);
        inherentMap.put("MUL", 0x3D);
        inherentMap.put("DAA", 0x19);

    // JMP and JSR removed

        // ===== LDX, LDY, LDS, LDU (16-bit) =====
        Map<AddressingModeType, Integer> ldxMap = new HashMap<>();
        ldxMap.put(AddressingModeType.IMMEDIATE, 0x8E);
        ldxMap.put(AddressingModeType.DIRECT, 0x9E);
        ldxMap.put(AddressingModeType.INDEXED, 0xAE);
        ldxMap.put(AddressingModeType.EXTENDED, 0xBE);
        ldxMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xBE);
        opcodeMap.put("LDX", ldxMap);
        
        Map<AddressingModeType, Integer> ldyMap = new HashMap<>();
        ldyMap.put(AddressingModeType.IMMEDIATE, 0x10CE);
        ldyMap.put(AddressingModeType.DIRECT, 0x109E);
        ldyMap.put(AddressingModeType.INDEXED, 0x10AE);
        ldyMap.put(AddressingModeType.EXTENDED, 0x10BE);
        ldyMap.put(AddressingModeType.EXTENDED_INDIRECT, 0x10BE);
        opcodeMap.put("LDY", ldyMap);
        
        Map<AddressingModeType, Integer> ldsMap = new HashMap<>();
        ldsMap.put(AddressingModeType.IMMEDIATE, 0x10FE);
        ldsMap.put(AddressingModeType.DIRECT, 0x109F);
        ldsMap.put(AddressingModeType.INDEXED, 0x10AF);
        ldsMap.put(AddressingModeType.EXTENDED, 0x10BF);
        ldsMap.put(AddressingModeType.EXTENDED_INDIRECT, 0x10BF);
        opcodeMap.put("LDS", ldsMap);
        
        Map<AddressingModeType, Integer> lduMap = new HashMap<>();
        lduMap.put(AddressingModeType.IMMEDIATE, 0xCE);
        lduMap.put(AddressingModeType.DIRECT, 0xDE);
        lduMap.put(AddressingModeType.INDEXED, 0xEE);
        lduMap.put(AddressingModeType.EXTENDED, 0xFE);
        lduMap.put(AddressingModeType.EXTENDED_INDIRECT, 0xFE);
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

        // ===== INSTRUCTIONS LEA (Load Effective Address) =====
        Map<AddressingModeType, Integer> leaxMap = new HashMap<>();
        leaxMap.put(AddressingModeType.INDEXED, 0x30);
        opcodeMap.put("LEAX", leaxMap);
        
        Map<AddressingModeType, Integer> leayMap = new HashMap<>();
        leayMap.put(AddressingModeType.INDEXED, 0x31);
        opcodeMap.put("LEAY", leayMap);
        
        Map<AddressingModeType, Integer> leasMap = new HashMap<>();
        leasMap.put(AddressingModeType.INDEXED, 0x32);
        opcodeMap.put("LEAS", leasMap);
        
        Map<AddressingModeType, Integer> leauMap = new HashMap<>();
        leauMap.put(AddressingModeType.INDEXED, 0x33);
        opcodeMap.put("LEAU", leauMap);

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
            if (map != null) {
                Integer result = map.get(mode);
                //System.out.println("DEBUG getOpcode: mnemonic=" + mnemonic + " mode=" + mode + " result=" + result);
                return result;
            }
            //System.out.println("DEBUG getOpcode: NO MAP FOUND for mnemonic=" + mnemonic);
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
        map.put(AddressingModeType.INDIRECT_INDEXED, idx); // Indirect indexed utilise le même opcode que INDEXED
        map.put(AddressingModeType.EXTENDED, ext);
        map.put(AddressingModeType.EXTENDED_INDIRECT, ext); // Indirect utilise le même opcode que EXTENDED
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
     * Retourne le code numérique d'un registre pour EXG et TFR
     * 0=A, 1=B, 2=CC, 3=DP, 4=D, 5=X, 6=Y, 7=U, 8=S
     */
    private int getRegisterCode(String reg) {
        switch (reg) {
            case "A": return 0;
            case "B": return 1;
            case "CC": return 2;
            case "DP": return 3;
            case "D": return 4;
            case "X": return 5;
            case "Y": return 6;
            case "U": return 7;
            case "S": return 8;
            default: return -1;
        }
    }}