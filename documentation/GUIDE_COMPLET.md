# Simulateur Motorola 6809
## Guide Complet du Projet Académique

---

## TABLE DES MATIÈRES

1. Installation et Lancement
2. Guide d'Utilisation
3. Architecture Logicielle
4. Format Assembleur Supporté
5. Instructions Implémentées
6. Algorithme d'Assemblage
7. Documentation Technique Interne
8. Débogage Avancé

---

## 1. INSTALLATION ET LANCEMENT

### Prérequis
- Java JDK 11 ou supérieur
- Windows, Linux, ou macOS

### Compilation
```bash
# Windows
compile.bat

# Linux/macOS
chmod +x compile.sh
./compile.sh
```

### Exécution
```bash
# Windows
run.bat

# Linux/macOS
./run.sh
# ou directement
java -cp bin motobatata.gui.SimulatorFrame
```

### Structure du Projet
```
motobatata/
├── src/motobatata/
│   ├── addressing/       # Modes d'adressage
│   ├── assembler/        # Assembleur 6809
│   ├── cpu/              # Cœur du CPU et débogueur
│   ├── decoder/          # Décodeur d'instructions
│   ├── gui/              # Interface graphique
│   ├── instructions/     # Toutes les instructions
│   └── memory/           # Gestion de la mémoire
├── bin/                  # Fichiers compilés
├── compile.bat/sh        # Scripts de compilation
├── run.bat/sh            # Scripts d'exécution
└── README.md
```

---

## 2. GUIDE D'UTILISATION

### Interface Principale

#### Panneau Gauche : Registres et Flags
- **A (8-bit)** : Accumulateur A
- **B (8-bit)** : Accumulateur B
- **X (16-bit)** : Registre d'index X
- **Y (16-bit)** : Registre d'index Y
- **PC (16-bit)** : Program Counter (Compteur de Programme)
- **S (16-bit)** : Stack Pointer (Pointeur de Pile)
- **CC (8-bit)** : Condition Code Register (Drapeaux)

**TOUS LES REGISTRES SONT ÉDITABLES** : Cliquez sur un champ et entrez une valeur hexadécimale

#### Panneau Central-Haut : Éditeur de Code Assembleur
- Écrivez du code assembleur 6809
- Exemple fourni par défaut
- Non compilé automatiquement (doit être compilé par l'utilisateur)

#### Panneau Central-Bas : Mémoire
- **Adresse** : Affichage hexadécimal 0x0000 - 0xFFFF
- **Hex** : Les 16 bytes en hexadécimal
- **ASCII** : Représentation ASCII des bytes
- **Boutons** : Navigation (▲▼) et accès direct

#### Panneau Droit : UAL (Unité Arithmétique et Logique)
- Représentation visuelle de l'UAL
- Montre le flux de données

#### Panneau Bas : Contrôle et Débogage

**Boutons :**
- **STEP** : Exécute UNE instruction
- **RUN** : Exécution continue (s'arrête aux breakpoints)
- **PAUSE** : Interrompt l'exécution
- **RESET** : Réinitialise le CPU

**Breakpoints :**
- **Add Breakpoint** : Entrez une adresse en hexadécimal (ex: 0x1000)
- **Clear Breakpoints** : Efface tous les breakpoints

### Workflow Typique

1. **Écrire du code** dans l'éditeur de code
2. **Compiler** (fonctionnalité à ajouter)
3. **Charger en mémoire** à l'adresse 0x0000
4. **Définir breakpoints** si souhaité
5. **Exécuter** :
   - STEP pour exécution instruction par instruction
   - RUN pour exécution continue
6. **Observer** :
   - Changements dans les registres
   - Modifications de la mémoire
   - État des flags (CC register)
7. **Déboguer** si comportement inattendu

---

## 3. ARCHITECTURE LOGICIELLE

### Packages Principaux

#### `motobatata.cpu`
- **CPU.java** : Cœur du microprocesseur 6809
  - Gestion de tous les registres (A, B, X, Y, U, S, PC, CC)
  - Pile (stack management)
  - Fetch/Decode/Execute cycle
  
- **Debugger.java** : Débogueur intégré
  - Breakpoints
  - Exécution pas à pas
  - Inspection de l'état

#### `motobatata.memory`
- **Memory.java** : Mémoire RAM 64KB
  - Lecture/écriture byte et word
  - Gestion des adresses 16-bit

#### `motobatata.instructions`
- **Instruction.java** : Interface pour toutes les instructions
- **40+ fichiers** : Une classe par opcode
  - Arithmétique : ADD, SUB, ADDD, SUBD
  - Logique : AND, OR, EOR
  - Chargement/Stockage : LD, ST
  - Branchements : BRA, BEQ, JSR, LBRA, LBSR
  - Autres : NOP, HALT, SWI

#### `motobatata.addressing`
- **AddressingMode.java** : Calcul des adresses
  - Immédiat (8-bit et 16-bit)
  - Direct (page zéro)
  - Étendu (16-bit)
  - Indexé (X, Y, U, S, PC)

#### `motobatata.decoder`
- **InstructionDecoder.java** : Mapping opcode → Instruction
  - Table d'opcodes
  - Factory pour créer les instructions

#### `motobatata.assembler`
- **Assembler.java** : Convertit ASM → bytecode
  - Parsing des mnémoniques
  - Résolution des modes d'adressage

#### `motobatata.gui`
- **SimulatorFrame.java** : Fenêtre principale
- **ControlPanel.java** : Commandes d'exécution et breakpoints
- **RegisterPanel.java** : Affichage/édition des registres
- **MemoryPanel.java** : Affichage de la mémoire
- **CodeEditorPanel.java** : Éditeur de code
- **FlagsPanel.java** : Visualisation des flags CC
- **UALPanel.java** : Représentation de l'UAL

### Flux de Données

```
┌─────────────────────────────────────────────────┐
│ Utilisateur écrit code assembleur               │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ Assembler.parse()    │
        │ - Tokenize ASM       │
        │ - Résout adresses    │
        └──────────┬───────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ Bytecode généré      │
        │ Chargé en mémoire    │
        └──────────┬───────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │ CPU.step()           │
        │ 1. Fetch             │
        │ 2. Decode            │
        │ 3. Execute           │
        └──────────┬───────────┘
                   │
    ┌──────────────┼──────────────┐
    ▼              ▼              ▼
┌─────────┐  ┌──────────┐  ┌────────────┐
│Registres│  │Mémoire   │  │Flags (CC)  │
│Modifiés │  │Modifiée  │  │Modifiés    │
└────┬────┘  └────┬─────┘  └────┬───────┘
     └────────────┼─────────────┘
                  ▼
           ┌────────────────┐
           │GUI Mise à jour │
           └────────────────┘
```

---

## 4. FORMAT ASSEMBLEUR SUPPORTÉ

### Syntaxe Générale

```asm
; Commentaires commencent par un point-virgule

; Étiquette (label)
LOOP:
    LDA #$10        ; Charger 0x10 dans A
    ADDA #$05       ; Ajouter 0x05 à A
    STA $20         ; Stocker A à l'adresse 0x0020
    BRA LOOP        ; Sauter à LOOP

; Hex sans $ est décimal
DECIMAL: LDA 16     ; Charge la valeur 16 (décimal)
HEX:     LDA $10    ; Charge la valeur 0x10 (hex)
```

### Modes d'Adressage

#### 1. Mode Immédiat
```asm
LDA #$10        ; Load A with immediate value 0x10
ADDD #$1234     ; Add 0x1234 to D
```

#### 2. Mode Direct (Page Zéro)
```asm
LDA <$20        ; Load A from address 0x0020
STA <$30        ; Store A at address 0x0030
```

#### 3. Mode Étendu
```asm
LDA $1234       ; Load A from address 0x1234
STA $5678       ; Store A at address 0x5678
```

#### 4. Mode Indexé
```asm
LDA ,X          ; Load A from address in X
LDA 5,X         ; Load A from address X+5
LDA -1,X        ; Load A from address X-1
STA ,Y          ; Store A at address in Y
LDA 10,U        ; Load from U+10
```

#### 5. Mode Relatif (pour branchements 8-bit)
```asm
BRA LABEL       ; Branch to LABEL (auto-calcul offset)
BEQ LABEL       ; Branch if equal
```

#### 6. Mode Relatif Long (16-bit)
```asm
LBRA LABEL      ; Long branch always
LBSR SUB        ; Long branch to subroutine
```

### Exemples de Code Complet

**Exemple 1 : Somme simple**
```asm
        ORG $0000       ; Origin - start at 0x0000
        LDA #$05        ; A = 5
        ADDA #$03       ; A = A + 3 = 8
        STA $0100       ; Stocke le résultat
        BRA *           ; Infini (branche à elle-même)
```

**Exemple 2 : Boucle**
```asm
        LDD #$0000      ; D = 0
LOOP:   ADDD #$0001     ; D++
        CMPD #$0010     ; Comparer D avec 16
        BNE LOOP        ; Si différent, continuer
        STD $0100       ; Stocker le résultat
        BRA *           ; Fin
```

**Exemple 3 : Appel de fonction**
```asm
START:  JSR SUB1        ; Appel la subroutine
        BRA *           ; Fin

SUB1:   LDA #$0A
        ADDA #$05
        RTS             ; Retour
```

---

## 5. INSTRUCTIONS IMPLÉMENTÉES

### Catégories

#### Arithmétique 8-bit
| Opcode | Fonction | Notes |
|--------|----------|-------|
| ADDA | A = A + operand | 8-bit |
| ADDB | B = B + operand | 8-bit |
| SUBA | A = A - operand | 8-bit |
| SUBB | B = B - operand | 8-bit |
| CMP | Flags = A - operand | Sans stockage |
| CMPA | Compare A | Alias CMP |

#### Arithmétique 16-bit
| Opcode | Fonction | Notes |
|--------|----------|-------|
| ADDD | D = D + operand | 16-bit sur A:B |
| SUBD | D = D - operand | 16-bit |
| CMPD | Flags = D - operand | Sans stockage |
| LDD | D = operand | Chargement 16-bit |
| STD | memory = D | Stockage 16-bit |

#### Logique
| Opcode | Fonction |
|--------|----------|
| ANDA | A = A AND operand |
| ANDB | B = B AND operand |
| ORA | A = A OR operand |
| ORB | B = B OR operand |
| EORA | A = A XOR operand |
| EORB | B = B XOR operand |
| BIT | Flags = A AND operand |

#### Chargement/Stockage 8-bit
| Opcode | Fonction |
|--------|----------|
| LDA | A = memory |
| LDB | B = memory |
| STA | memory = A |
| STB | memory = B |

#### Déplacement/Rotation
| Opcode | Fonction | Drapeaux |
|--------|----------|---------|
| ASL | Décalage arithmétique gauche | N Z V C |
| ASR | Décalage arithmétique droite | N Z V C |
| LSL | Décalage logique gauche | N Z V C |
| LSR | Décalage logique droite | N Z V C |
| ROL | Rotation gauche | N Z V C |
| ROR | Rotation droite | N Z V C |

#### Branchements conditionnels
| Opcode | Condition | Flags |
|--------|-----------|-------|
| BEQ | Égal | Z |
| BNE | Non égal | !Z |
| BGE | ≥ signé | !(N^V) |
| BGT | > signé | !(N^V) && !Z |
| BLE | ≤ signé | (N^V) \| Z |
| BLT | < signé | N^V |
| BCC | Pas de carry | !C |
| BCS | Carry | C |
| BMI | Négatif | N |
| BPL | Positif | !N |
| BVC | Pas overflow | !V |
| BVS | Overflow | V |
| BRA | Toujours | - |
| BRN | Jamais | - |

#### Branchements longs (16-bit)
| Opcode | Fonction |
|--------|----------|
| LBRA | Long branch always (16-bit offset) |
| LBSR | Long branch to subroutine (16-bit) |
| LBEQ | Long branch if equal (16-bit) |

#### Appels et Retours
| Opcode | Fonction |
|--------|----------|
| JSR | Jump to subroutine (8-bit) |
| LBSR | Jump long to subroutine (16-bit) |
| RTS | Return from subroutine |
| RTI | Return from interrupt |

#### Registres spéciaux
| Opcode | Fonction |
|--------|----------|
| TFR | Transfer register |
| EXG | Exchange registers |
| PSH | Push multiple registers |
| PUL | Pull multiple registers |
| PSHS | Push to S stack |
| PULS | Pull from S stack |

#### Autres
| Opcode | Fonction |
|--------|----------|
| NOP | Pas d'opération |
| SYNC | Synchroniser |
| SWI | Software interrupt |
| CWAI | Clear wait interrupt |
| DAA | Decimal adjust |
| LEA | Load effective address |

---

## 6. ALGORITHME D'ASSEMBLAGE

### Vue d'ensemble

```
┌──────────────────────────┐
│ Code Source Assembleur   │
│ (*.asm ou texte)         │
└────────────┬─────────────┘
             │
             ▼
    ┌────────────────────┐
    │ Passe 1: Parsing   │
    │ - Tokenization     │
    │ - Labels → adresses│
    │ - Symboles → table │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │ Passe 2: Génération│
    │ - Résolution addr  │
    │ - Génération code  │
    │ - Bytecode output  │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │ Bytecode généré    │
    │ (Machine code)     │
    └────────────────────┘
```

### Passe 1 : Construction de la Table de Symboles

```java
HashMap<String, Integer> symbolTable = new HashMap<>();
int currentAddress = 0x0000;

for (String line : sourceCode.split("\n")) {
    String trimmed = line.trim();
    
    // Ignorer les commentaires
    if (trimmed.startsWith(";")) continue;
    if (trimmed.isEmpty()) continue;
    
    // Extraire label (nom:)
    if (trimmed.contains(":")) {
        String label = trimmed.substring(0, trimmed.indexOf(":"));
        symbolTable.put(label, currentAddress);
        trimmed = trimmed.substring(trimmed.indexOf(":") + 1).trim();
        
        if (trimmed.isEmpty()) continue;
    }
    
    // Calculer la taille de l'instruction
    String mnemonic = trimmed.split("\\s+")[0];
    int size = getInstructionSize(mnemonic);
    currentAddress += size;
}
```

### Passe 2 : Génération du Bytecode

```java
byte[] bytecode = new byte[0x10000];
int currentAddress = 0x0000;

for (String line : sourceCode.split("\n")) {
    // ... parsing similaire à passe 1 ...
    
    String[] parts = trimmed.split("\\s+");
    String mnemonic = parts[0];
    String operand = (parts.length > 1) ? parts[1] : "";
    
    // Résoudre l'operand
    int operandValue;
    if (operand.startsWith("#$")) {
        operandValue = Integer.parseInt(operand.substring(2), 16);
    } else if (operand.startsWith("$")) {
        operandValue = Integer.parseInt(operand.substring(1), 16);
    } else if (symbolTable.containsKey(operand)) {
        operandValue = symbolTable.get(operand);
    } else {
        operandValue = Integer.parseInt(operand, 10);
    }
    
    // Encoder l'instruction
    byte[] encoded = encodeInstruction(mnemonic, operandValue);
    System.arraycopy(encoded, 0, bytecode, currentAddress, encoded.length);
    currentAddress += encoded.length;
}
```

### Exemple : Assemblage de "LDA #$10"

1. **Tokenization**
   - Mnemonic: "LDA"
   - Operand: "#$10"

2. **Résolution opérande**
   - Format: Immediate 8-bit
   - Valeur: 0x10

3. **Recherche dans la table d'opcodes**
   - LDA immediate = 0x86

4. **Génération bytecode**
   - Byte 1: 0x86 (opcode)
   - Byte 2: 0x10 (valeur immédiate)

5. **Résultat en mémoire**
   - [PC]: 0x86 0x10

---

## 7. DOCUMENTATION TECHNIQUE INTERNE

### Registres du 6809

#### Registres d'accumulation (8-bit)
- **A** : Accumulateur principal
- **B** : Accumulateur secondaire
- **D** : Paire A:B (16-bit) - A en poids fort, B en poids faible

#### Registres d'index (16-bit)
- **X** : Registre d'index X
- **Y** : Registre d'index Y
- **U** : User stack pointer
- **S** : System stack pointer

#### Registres de contrôle (16-bit)
- **PC** : Program Counter (Compteur de programme)

#### Registre de code condition (8-bit)
```
Bit 7 (E) : Entire flag (sauvegarde complète)
Bit 6     : Flag
Bit 5 (F) : Fast interrupt flag
Bit 4 (H) : Half-carry flag
Bit 3 (I) : Interrupt flag
Bit 2 (N) : Negative flag
Bit 1 (Z) : Zero flag
Bit 0 (C) : Carry flag
```

### Drapeaux (Condition Code Register)

| Bit | Nom | Signification |
|-----|-----|---------------|
| 0 | C (Carry) | Retenue après opération 8-bit |
| 1 | V (oVerflow) | Débordement signé |
| 2 | Z (Zero) | Résultat = 0 |
| 3 | N (Negative) | Bit de signe du résultat |
| 4 | H (Half-carry) | Retenue en bit 3 (BCD) |
| 5 | I (Interrupt) | Masque d'interruption |
| 6 | F (Fast interrupt) | Masque d'interruption rapide |
| 7 | E (Entire) | Sauvegarde complète des registres |

### Pile (Stack)

Le 6809 utilise une pile **full-descending** :
- **PUSH** : S décrémente, puis écrit
- **POP** : Lit, puis S incrémente

```
    S pointeur avant PUSH
    │
    ▼
  ┌───┐
  │...│
  ├───┤
  │...│ ← S pointeur après PUSH/avant POP
  ├───┤
  │...│
  └───┘
```

### Modes d'Adressage et Calculs

#### Immédiat
```
Mémoire : [PC]   = opcode
         [PC+1]  = valeur
Résultat: opérande = [PC+1]
PC update: PC += 2
```

#### Direct (page zéro)
```
Mémoire : [PC]   = opcode
         [PC+1]  = adresse faible (0x00-0xFF)
Résultat: opérande = mémoire[0x00XX]
PC update: PC += 2
```

#### Étendu
```
Mémoire : [PC]   = opcode
         [PC+1]  = adresse haute
         [PC+2]  = adresse basse
Résultat: opérande = mémoire[adresse]
PC update: PC += 3
```

#### Indexé
```
Post-byte: RRPPMMBB
    RR = Registre (00=X, 01=Y, 10=U, 11=S)
    PP = Offset (00=, 01=+1, 10=-1, 11=acc)
    MM = Mode (00=indirect, 01=A, 10=B, 11=D)
    BB = Bit indirect
```

---

## 8. DÉBOGAGE AVANCÉ

### Utilisation du Débogueur

#### Breakpoints
```java
// Ajouter un breakpoint
cpu.getDebugger().addBreakpoint(0x1000);

// Exécuter avec arrêt automatique
cpu.getDebugger().run(); // S'arrête à 0x1000

// Afficher les breakpoints
Set<Integer> breakpoints = cpu.getDebugger().getBreakpoints();
for (int bp : breakpoints) {
    System.out.println(String.format("Breakpoint: 0x%04X", bp));
}
```

#### Inspection de l'état
```java
// État du CPU
int a = cpu.getAccA();
int pc = cpu.getRegPC();
boolean z = cpu.getFlag(CPU.CC_Z);
boolean c = cpu.getFlag(CPU.CC_C);

// État de la mémoire
int byte_val = memory.readByte(0x1000);
int word_val = memory.readWord(0x1000);
```

#### Trace d'exécution
```java
for (int i = 0; i < 100; i++) {
    int pc = cpu.getRegPC();
    System.out.println(String.format("PC=0x%04X, A=0x%02X, B=0x%02X",
        pc, cpu.getAccA(), cpu.getAccB()));
    cpu.getDebugger().step();
}
```

### Dépannage Courant

**Problème : Infini/Boucle**
- Vérifier que le branchement n'est pas récursif
- Utiliser STEP pour tracer
- Ajouter breakpoint pour voir l'adresse

**Problème : Valeur fausse en A/B**
- Vérifier le mode d'adressage
- Éditer les registres manuellement pour tester
- Tracer l'instruction avec STEP

**Problème : Erreur lors du chargement**
- Vérifier la syntaxe assembleur
- Vérifier que les labels existent
- Vérifier les adresses en hexadécimal (#$ pour immédiat)

---

## CONCLUSION

Ce simulateur fournit un environnement pédagogique complet pour l'étude du Motorola 6809. 
Il combine :
- ✅ Implémentation complète du CPU
- ✅ Interface graphique intuitive
- ✅ Débogueur intégré
- ✅ Support des modes d'adressage
- ✅ Documentation complète

Pour plus d'informations, consultez le code source avec les commentaires détaillés.

---

**Version**: 1.0  
**Date**: 21 Décembre 2025  
**Auteur**: Projet Académique 6809
