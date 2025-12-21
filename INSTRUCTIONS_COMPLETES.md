# Instructions Supportées - Motorola 6809 Simulator

## 📋 Table Complète des Instructions

### 🔧 Instructions de Chargement (Load)

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **LDA** | `LDA #$42` | `LDA <$80` | `LDA 10,X` | `LDA $1000` |
| **LDB** | `LDB #$42` | `LDB <$80` | `LDB 10,X` | `LDB $1000` |
| **LDX** | `LDX #$4200` | `LDX <$80` | `LDX 10,X` | `LDX $1000` |
| **LDY** | `LDY #$4200` | `LDY <$80` | `LDY 10,X` | `LDY $1000` |

### 💾 Instructions de Stockage (Store)

| Instruction | Direct | Indexé | Étendu |
|------------|--------|--------|--------|
| **STA** | `STA <$80` | `STA 10,X` | `STA $1000` |
| **STB** | `STB <$80` | `STB 10,X` | `STB $1000` |
| **STX** | `STX <$80` | `STX 10,X` | `STX $1000` |
| **STY** | `STY <$80` | `STY 10,X` | `STY $1000` |

### ➕ Instructions d'Addition

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **ADDA** | `ADDA #$42` | `ADDA <$80` | `ADDA 10,X` | `ADDA $1000` |
| **ADDB** | `ADDB #$42` | `ADDB <$80` | `ADDB 10,X` | `ADDB $1000` |

### ➖ Instructions de Soustraction

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **SUBA** | `SUBA #$42` | `SUBA <$80` | `SUBA 10,X` | `SUBA $1000` |
| **SUBB** | `SUBB #$42` | `SUBB <$80` | `SUBB 10,X` | `SUBB $1000` |

### 🔢 Instructions Avec Carry

| Instruction | Type | Exemple |
|------------|------|---------|
| **ADCA** | Add with Carry | `ADCA #$10` |
| **ADCB** | Add with Carry | `ADCB #$10` |
| **SBCA** | Subtract with Carry | `SBCA #$10` |
| **SBCB** | Subtract with Carry | `SBCB #$10` |

### 🔗 Instructions Logiques ET (AND)

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **ANDA** | `ANDA #$F0` | `ANDA <$80` | `ANDA 10,X` | `ANDA $1000` |
| **ANDB** | `ANDB #$F0` | `ANDB <$80` | `ANDB 10,X` | `ANDB $1000` |

### 🔗 Instructions Logiques OU (OR)

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **ORA** | `ORA #$0F` | `ORA <$80` | `ORA 10,X` | `ORA $1000` |
| **ORB** | `ORB #$0F` | `ORB <$80` | `ORB 10,X` | `ORB $1000` |

### 🔗 Instructions OU Exclusif (EOR)

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **EORA** | `EORA #$AA` | `EORA <$80` | `EORA 10,X` | `EORA $1000` |
| **EORB** | `EORB #$AA` | `EORB <$80` | `EORB 10,X` | `EORB $1000` |

### 🔍 Instructions de Comparaison

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **CMPA** | `CMPA #$42` | `CMPA <$80` | `CMPA 10,X` | `CMPA $1000` |
| **CMPB** | `CMPB #$42` | `CMPB <$80` | `CMPB 10,X` | `CMPB $1000` |
| **CMPD** | `CMPD #$4200` | - | - | - |

### 🎯 Instructions Test de Bit

| Instruction | Immédiat | Direct | Indexé | Étendu |
|------------|----------|--------|--------|--------|
| **BITA** | `BITA #$80` | `BITA <$80` | `BITA 10,X` | `BITA $1000` |
| **BITB** | `BITB #$80` | `BITB <$80` | `BITB 10,X` | `BITB $1000` |

### 🔄 Instructions Unaires (Registre A)

| Instruction | Exemple | Effet |
|------------|---------|--------|
| **CLRA** | `CLRA` | A = 0 |
| **INCA** | `INCA` | A++ |
| **DECA** | `DECA` | A-- |
| **COMA** | `COMA` | A = ~A (complément) |
| **NEGA** | `NEGA` | A = -A |
| **TSTA** | `TSTA` | Test A (set flags) |

### 🔄 Instructions Unaires (Registre B)

| Instruction | Exemple | Effet |
|------------|---------|--------|
| **CLRB** | `CLRB` | B = 0 |
| **INCB** | `INCB` | B++ |
| **DECB** | `DECB` | B-- |
| **COMB** | `COMB` | B = ~B |
| **NEGB** | `NEGB` | B = -B |
| **TSTB** | `TSTB` | Test B |

### 🔀 Instructions de Rotation/Décalage

| Instruction | Description |
|------------|-------------|
| **ASRA** | Arithmetic Shift Right A |
| **ASRB** | Arithmetic Shift Right B |
| **LSRA** | Logical Shift Right A |
| **LSRB** | Logical Shift Right B |
| **ROLA** | Rotate Left A |
| **ROLB** | Rotate Left B |
| **RORA** | Rotate Right A |
| **RORB** | Rotate Right B |
| **LSLA** | Logical Shift Left A |
| **LSLB** | Logical Shift Left B |

### 🏃 Branchements (8-bit relatif)

| Instruction | Condition | Exemple |
|------------|-----------|---------|
| **BRA** | Always | `BRA LABEL` |
| **BRN** | Never | `BRN LABEL` |
| **BEQ** | Equal (Z=1) | `BEQ LABEL` |
| **BNE** | Not Equal (Z=0) | `BNE LABEL` |
| **BCC** | Carry Clear (C=0) | `BCC LABEL` |
| **BCS** | Carry Set (C=1) | `BCS LABEL` |
| **BPL** | Plus (N=0) | `BPL LABEL` |
| **BMI** | Minus (N=1) | `BMI LABEL` |
| **BVC** | Overflow Clear | `BVC LABEL` |
| **BVS** | Overflow Set | `BVS LABEL` |
| **BGE** | Greater or Equal | `BGE LABEL` |
| **BLT** | Less Than | `BLT LABEL` |
| **BGT** | Greater Than | `BGT LABEL` |
| **BLE** | Less or Equal | `BLE LABEL` |
| **BHI** | Higher (unsigned) | `BHI LABEL` |
| **BLS** | Lower or Same | `BLS LABEL` |
| **BSR** | Branch to Subroutine | `BSR LABEL` |

### ⏭️ Sauts

| Instruction | Mode | Exemple |
|------------|------|---------|
| **JMP** | Indexed | `JMP 10,X` |
| **JMP** | Extended | `JMP $1000` |
| **JSR** | Indexed | `JSR 10,X` |
| **JSR** | Extended | `JSR $1000` |

### 🛑 Instructions Spéciales

| Instruction | Exemple | Effet |
|------------|---------|--------|
| **NOP** | `NOP` | No Operation |
| **RTS** | `RTS` | Return from Subroutine |
| **RTI** | `RTI` | Return from Interrupt |
| **SWI** | `SWI` | Software Interrupt |
| **ABX** | `ABX` | Add B to X |
| **MUL** | `MUL` | Multiply A × B → D |
| **DAA** | `DAA` | Decimal Adjust A |

## 📝 Exemples de Code Complet

### Exemple 1 : Addition Simple
```asm
LDA #$05
ADDA #$03
STA $20        ; Stocke le résultat
```

### Exemple 2 : Boucle
```asm
LDA #$00       ; Initialiser A
LOOP:
ADDA #$01      ; Incrémenter
CMPA #$0A      ; Comparer avec 10
BNE LOOP       ; Recommencer si pas égal
```

### Exemple 3 : Utilisation de Registres
```asm
LDB #$42
LDA #$08
ADDA B         ; Ajouter B à A
STA $30
```

### Exemple 4 : Opérations Logiques
```asm
LDA #$F0
ANDA #$0F      ; Masque les bits bas
ORA #$03       ; Active les bits 0-1
```

## ✅ Notes Importantes

1. **Format Hexadécimal** : Utilisez `$` ou `0x` pour les nombres hexadécimaux
2. **Commentaires** : Commencent par `;`
3. **Labels** : Finissent par `:`
4. **Modes d'Adressage** :
   - `#$42` = Immédiat
   - `<$80` = Direct (8-bit)
   - `$1000` = Étendu (16-bit)
   - `10,X` = Indexé
5. **Accumulateurs** : A et B (8-bit), D (16-bit combiné)
6. **Registres Index** : X, Y, U, S

## 🎯 Statut des Mnémoniques

- ✅ Tous les mnémoniques sont reconnus par l'Assembler
- ✅ Tous les modes d'adressage sont supportés
- ✅ Les opcodes sont conformes au Motorola 6809
- ✅ Les flags CC sont correctement mis à jour

