# Guide d'Utilisation - Simulateur Motorola 6809

## 🎯 Workflow Complet

Le simulateur fonctionne maintenant avec un workflow complet en 3 étapes :

### 1️⃣ **ÉCRIRE** - Éditer le code assembleur
- Utilisez l'éditeur de code en haut au centre
- Écrivez votre code assembleur 6809
- Les lignes de commentaires commencent par `;`

**Exemple de code :**
```asm
; Programme simple : addition
LDA #$10       ; Charger 0x10 dans A
ADDA #$05      ; Ajouter 0x05
STA $20        ; Stocker le résultat à l'adresse 0x20
```

### 2️⃣ **COMPILER** - Assembler le code
- Cliquez sur le bouton vert **COMPILER** dans le panneau de contrôle en bas
- Le code assembleur est converti en bytecode
- Un message confirme le succès : "✓ Compilation réussie - XX octets chargés"
- L'adresse PC (Program Counter) revient à 0x0000
- Les registres s'affichent à gauche en temps réel

### 3️⃣ **EXÉCUTER** - Lancer le programme
Vous avez plusieurs options :

#### **STEP** - Exécution pas à pas
- Exécute une instruction à la fois
- Parfait pour déboguer
- Les registres se mettent à jour après chaque instruction

#### **RUN** - Exécution continue
- Lance l'exécution automatique du programme
- Exécute jusqu'à la fin ou jusqu'à un breakpoint
- Délai court entre chaque instruction pour visualiser

#### **PAUSE** - Arrêter l'exécution
- Arrête le programme en cours d'exécution
- Vous pouvez relancer avec RUN ou avancer avec STEP

#### **RESET** - Réinitialiser
- Remet la CPU à zéro
- Efface les breakpoints
- Vous devez recompiler le code

## 📊 Panneaux de l'Interface

### Gauche : **REGISTRES & FLAGS**
- Affiche tous les registres (A, B, D, X, Y, U, S, PC, DP)
- Affiche les flags (N, Z, V, C, etc.)
- Se mettent à jour en temps réel

### Centre Haut : **ÉDITEUR DE CODE**
- Édition du code assembleur
- Statut de compilation affiché en bas

### Centre Bas : **MÉMOIRE**
- Affiche la mémoire RAM de la CPU
- Vous pouvez naviguer et voir l'état des données

### Droit : **UNITÉ ARITHMÉTIQUE ET LOGIQUE (UAL)**
- Affiche les opérations effectuées
- Informations de débogage

### Bas : **PANNEAU DE CONTRÔLE**
- Boutons d'exécution
- Boutons de gestion des breakpoints
- Statut et messages

## 🎮 Exemple Complet d'Utilisation

### Étape 1 : Écrire le code
Remplacez le contenu de l'éditeur par :
```asm
; Test simple
LDA #$05
ADDA #$03
```

### Étape 2 : Compiler
- Cliquez sur **COMPILER**
- Message : "✓ Compilation réussie - 4 octets chargés"

### Étape 3 : Observer
- À gauche : Reg A = $00, PC = 0x0000
- Cliquez **STEP**
- À gauche : Reg A = $05, PC = 0x0001 (avance automatiquement)
- Cliquez **STEP** à nouveau
- À gauche : Reg A = $08, PC = 0x0002 (5 + 3 = 8)

## 🐛 Déboguer avec les Breakpoints

1. Cliquez sur **"Add Breakpoint"**
2. Entrez une adresse en hexadécimal (ex: `0001`)
3. L'exécution s'arrêtera à cette adresse
4. Utilisez **Clear Breakpoints** pour effacer tous les points d'arrêt

## ✨ Améliorations Apportées

Cette version inclut :
- ✅ **Intégration de l'assembleur** dans l'interface
- ✅ **Bouton de compilation** visible et accessible
- ✅ **Mise à jour en temps réel** des registres
- ✅ **Messages de statut** clairs et informatifs
- ✅ **Workflow intuitif** : Écrire → Compiler → Exécuter
- ✅ **Préservation de l'état** de la mémoire et des registres

## 📝 Instructions Supportées

- **Chargement** : LDA, LDB, LDD, LDX, LDY, LDU, LDS
- **Stockage** : STA, STB, STD, STX, STY, STU, STS
- **Arithmétique** : ADDA, ADDB, ADDD, SUBA, SUBB, SUBD
- **Logique** : ANDA, ANDB, ORA, ORB, EORA, EORB
- **Comparaison** : CMP, CMPD
- **Décalage** : ASR, ASL, LSR, LSL, ROL, ROR
- **Branchements** : BRA, BEQ, BNE, BLT, BGT, BLE, BGE, BMI, BPL
- **Sauts** : JSR, RTS, JMP
- **Divers** : NOP, CLR, INC, DEC, NEG, COM, etc.

## 🔧 Troubleshooting

### "Erreur de compilation : aucune instruction valide"
- Vérifiez qu'il y a du code valide dans l'éditeur
- Vérifiez la syntaxe (LDA #$10 au lieu de LDA$10)

### "Veuillez compiler le code d'abord"
- Cliquez sur **COMPILER** avant de cliquer **RUN** ou **STEP**

### Registres ne se mettent pas à jour
- Vérifiez que le code est compilé (bouton COMPILER)
- Cliquez **STEP** ou **RUN** pour exécuter
- Regardez à gauche le panneau des registres

## 📚 Ressources

Consultez les fichiers de documentation :
- [GUIDE_COMPLET.md](documentation/GUIDE_COMPLET.md)
- [README_SIMULATOR.md](README_SIMULATOR.md)
