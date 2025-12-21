# Simulateur Motorola 6809

Un émulateur complet, robuste et pédagogique du microprocesseur Motorola 6809 développé en Java avec interface graphique (Swing/AWT).

## 🎯 Objectif

Fournir un environnement d'émulation interactif pour l'apprentissage de l'architecture des microprocesseurs, la programmation assembleur 6809, et le débogage logiciel.

## ✨ Caractéristiques Principales

### 1. **Émulation Complète du CPU**
- Tous les registres du 6809 (A, B, X, Y, U, S, PC, CC)
- 40+ instructions implémentées
- Modes d'adressage : immédiat, direct, étendu, indexé
- Gestion complète de la pile (stack)
- Register Condition Code avec tous les drapeaux

### 2. **Débogueur Intégré**
- **Points d'arrêt (Breakpoints)** : Pause l'exécution à une adresse spécifique
- **Exécution pas à pas (Step)** : Exécute une instruction à la fois
- **Exécution continue (Run)** : Lance l'exécution jusqu'au breakpoint
- **Inspection de l'état** : Visualisez les registres et la mémoire en temps réel

### 3. **Interface Graphique Intuitive**
- 🖥️ **Panneau Registres** : Affichage et édition en temps réel
- 📝 **Éditeur de Code** : Écriture et visualisation de l'assembleur
- 🧠 **Visualisation Mémoire** : Navigation dans les 64KB de RAM
- ⚙️ **Panneau Contrôle** : STEP, RUN, PAUSE, RESET
- 🚦 **Drapeaux Visuels** : Indicateurs des flags CC

### 4. **Système d'Assemblage**
- Conversion automatique ASM → bytecode
- Support des labels et symboles
- Gestion des différents modes d'adressage
- Messages d'erreur informatifs

## 🚀 Installation et Utilisation

### Prérequis
- Java JDK 11 ou supérieur
- Aucune dépendance externe (utilise Swing/AWT standard)

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

# Ou directement
java -cp bin motobatata.gui.SimulatorFrame
```

## 📖 Guide d'Utilisation Rapide

1. **Écrire du code** dans le panneau "Éditeur de Code"
2. **STEP** : Exécuter une instruction
3. **RUN** : Exécution continue
4. **Ajouter Breakpoint** : Pause à une adresse donnée
5. **Observer** les changements de registres et mémoire

### Exemple de Code
```asm
; Exemple simple
LDA #$05        ; A = 5
ADDA #$03       ; A = A + 3 = 8
STA $0100       ; Stocker à l'adresse 0x0100
BRA *           ; Boucle infinie
```

## 📚 Instructions Supportées

### Arithmétique
- **8-bit** : ADDA, ADDB, SUBA, SUBB, CMP
- **16-bit** : ADDD, SUBD, CMPD, LDD, STD

### Logique
- ANDA, ANDB, ORA, ORB, EORA, EORB, BIT

### Chargement/Stockage
- LDA, LDB, STA, STB

### Branchements
- Conditionnels : BEQ, BNE, BGE, BLT, BCC, BCS, BMI, BPL, BVC, BVS
- Inconditionnels : BRA, LBRA
- Appels : JSR, LBSR, RTS

### Autres
- Déplacements : ASL, ASR, LSL, LSR, ROL, ROR
- Registres : TFR, EXG, PSH, PUL
- Spéciaux : NOP, SYNC, SWI, LEA

## 🏗️ Architecture

```
motobatata/
├── src/motobatata/
│   ├── cpu/              # Cœur du CPU + Debugger
│   ├── memory/           # Mémoire 64KB
│   ├── instructions/     # 40+ fichiers d'instructions
│   ├── addressing/       # Modes d'adressage
│   ├── decoder/          # Décodeur d'instructions
│   ├── assembler/        # Assembleur ASM
│   └── gui/              # Interface Swing/AWT
├── bin/                  # Fichiers compilés
├── documentation/        # Guides et documentation
└── compile.bat/sh        # Scripts de compilation
```

## 🔍 Cycles d'Exécution

```
┌─────────────────────┐
│ Fetch (PC → opcode) │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────┐
│ Decode (opcode → instr) │
└──────────┬──────────────┘
           │
           ▼
┌──────────────────────────────┐
│ Execute (compute + flags)    │
└──────────┬───────────────────┘
           │
           ▼
┌──────────────────────┐
│ Update GUI/Registres │
└──────────────────────┘
```

## 🛠️ Débogage Avancé

- Inspection de la mémoire avec navigation (Go to address)
- Édition manuelle des registres
- Affichage en temps réel des drapeaux (N, Z, V, C, H, I, F, E)
- Trace détaillée de l'exécution

## 📋 Modes d'Adressage

| Mode | Syntaxe | Exemple |
|------|---------|---------|
| Immédiat | `#$valeur` | `LDA #$10` |
| Direct | `<$adresse` | `LDA <$20` |
| Étendu | `$adresse` | `LDA $1234` |
| Indexé | `n,R` | `LDA 5,X` |
| Relatif | `LABEL` | `BEQ LOOP` |

## ✅ Conformité Académique

- ✅ Émulation complète du jeu d'instructions 6809
- ✅ Registres avec état en temps réel
- ✅ Débogueur avec breakpoints
- ✅ Exécution pas à pas
- ✅ Visualisation mémoire
- ✅ Interface graphique intuitive
- ✅ Documentation complète (PDF)
- ✅ Architecture modulaire et extensible

## 📖 Documentation

Voir [documentation/GUIDE_COMPLET.md](documentation/GUIDE_COMPLET.md) pour :
- Guide d'utilisation détaillé
- Architecture logicielle
- Format assembleur complet
- Algorithme d'assemblage
- Spécifications techniques
- Conseils de débogage

## 🎓 Cas d'Usage Pédagogique

- Apprentissage de l'architecture du 6809
- Programmation assembleur 6809
- Compréhension des modes d'adressage
- Débogage et trace d'exécution
- Simulation de microcontrôleurs

## 🔧 Extensibilité

Le code est conçu pour être facilement extensible :
- Ajouter une instruction : Créer une classe implémentant `Instruction.java`
- Ajouter un mode d'adressage : Ajouter une méthode dans `AddressingMode.java`
- Modifier l'interface : Éditer les fichiers GUI dans `motobatata.gui`

## 📝 Exemple d'Exécution

**Code:**
```asm
LDA #$05    ; A = 5
ADDA #$03   ; A = 8
STA $100    ; Mémoire[0x100] = 8
RTS
```

**Trace:**
```
PC=0x0000: LDA #$05  → A=0x05, Z=0
PC=0x0002: ADDA #$03 → A=0x08, Z=0
PC=0x0004: STA $100  → Mémoire[0x100]=0x08
PC=0x0007: RTS       → PC=(dépilé)
```

## ⚙️ Spécifications Techniques

- **Langage** : Java
- **GUI** : Java Swing/AWT
- **Mémoire** : 64KB adressable
- **CPU** : 6809 complet
- **Instructions** : 40+
- **Modes d'adressage** : 5+

## 🤝 Contributions

Les améliorations sont bienvenues :
- Corrections de bugs
- Nouvelles instructions
- Optimisations de performance
- Améliorations UI

## 📄 Licence

Projet académique - Usage libre pour fins éducatives

## 👨‍💼 Auteur

Projet académique - Année 2025

---

**Pour une utilisation optimale, consultez la [documentation complète](documentation/GUIDE_COMPLET.md)**
