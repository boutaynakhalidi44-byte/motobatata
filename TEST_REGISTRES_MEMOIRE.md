# Test de Validation - Registres et Mémoire Dynamiques

## 🧪 Procédure de Test

### Test 1 : STEP Mode (Pas à Pas)

**Objectif** : Vérifier que les registres et mémoire se mettent à jour à chaque instruction

1. **Écrire le code** dans l'éditeur :
```asm
LDA #$05
ADDA #$03
STA $20
```

2. **Cliquer COMPILER**
   - Vérifier : "✓ Compilation réussie - 6 octets chargés"
   - Vérifier : PC = 0x0000, A = 0x00

3. **Cliquer STEP (1ère fois)**
   - ✅ **A doit passer à 0x05** (chargement de 0x05)
   - ✅ **PC doit passer à 0x0001** (prochaine instruction)
   - ✅ **Mémoire inchangée** (on n'a pas encore écrit)

4. **Cliquer STEP (2e fois)**
   - ✅ **A doit passer à 0x08** (5 + 3 = 8)
   - ✅ **PC doit passer à 0x0002** 
   - ✅ **Mémoire inchangée**

5. **Cliquer STEP (3e fois)**
   - ✅ **A reste 0x08** (lecture)
   - ✅ **PC doit passer à 0x0003**
   - ✅ **Mémoire adresse 0x20 doit être 0x08** ✨

### Test 2 : RUN Mode (Exécution Continue)

**Objectif** : Vérifier que les registres et mémoire se mettent à jour pendant RUN

1. **Écrire le code** :
```asm
LDB #$10
LDA #$05
ADDA B
STA $30
```

2. **Cliquer COMPILER**
   - Vérifier : PC = 0x0000, A = 0x00, B = 0x00

3. **Cliquer RUN**
   - ✅ **Les valeurs changent progressivement** (avec délai de 100ms entre chaque)
   - ✅ **A passe à 0x10** (LDB)
   - ✅ **B passe à 0x05** (LDA) - Non, on charge dans A pas B
   
   **Correction - Revoir le code :**
   ```asm
   LDB #$10
   LDA #$05
   ADDA B
   STA $30
   ```
   
   - ✅ **B passe à 0x10** (LDB)
   - ✅ **A passe à 0x05** (LDA)
   - ✅ **A passe à 0x15** (ADDA B : 5 + 16 = 21 = 0x15)
   - ✅ **Mémoire 0x30 devient 0x15**
   - ✅ **PC avance d'instruction en instruction**

4. **Au bout d'environ 0.4 secondes**, le programme s'arrête automatiquement
   - ✅ **Tous les registres affichent les valeurs finales**
   - ✅ **Mémoire affiche les données stockées**

### Test 3 : PAUSE et Reprise

1. **Écrire du code long** (boucle avec plusieurs opérations)
2. **Cliquer RUN**
3. **Cliquer PAUSE après 1-2 secondes**
   - ✅ **L'exécution s'arrête immédiatement**
   - ✅ **Les registres affichent l'état actuel**
   - ✅ **Les mémoire affiche l'état actuel**

4. **Cliquer STEP**
   - ✅ **Une instruction s'exécute**
   - ✅ **Les registres/mémoire se mettent à jour**

### Test 4 : RESET

1. **Cliquer RESET**
   - ✅ **Tous les registres reviennent à 0x0000**
   - ✅ **Toute la mémoire est effacée**
   - ✅ **Message : "CPU Reset - Compilez le code à nouveau"**

2. **Cliquer RUN sans recompiler**
   - ✅ **Erreur : "Veuillez compiler le code d'abord"**

## ✅ Critères de Succès

| Feature | Critère |
|---------|---------|
| **STEP** | Les registres se mettent à jour après chaque clic |
| **RUN** | Les registres se mettent à jour tous les 100ms |
| **Mémoire** | Les données écrites (STA) apparaissent correctement |
| **Lecture Seule** | Impossible de modifier registres/mémoire à la main |
| **Synchronisation** | L'affichage reflète exactement l'état du CPU |
| **PAUSE** | Arrête l'exécution immédiatement |
| **RESET** | Nettoie complètement l'état |

## 🐛 Troubleshooting

### Problème : Les registres ne changent pas avec RUN
- ✅ **Solution** : Le délai est 100ms, attendez que plusieurs instructions s'exécutent
- ✅ **Vérifier** : PC devrait avancer de 0x0001 à 0x0002, etc.

### Problème : Mémoire n'affiche pas les changements
- ✅ **Solution** : Naviguez à l'adresse où on écrit (ex: adresse 0x20)
- ✅ **Vérifier** : Après STA $20, allez à l'adresse 0x0020

### Problème : STEP n'avance que de 1 instruction
- ✅ **Correct** : C'est le comportement normal de STEP
- ✅ **Utiliser RUN** : Pour plusieurs instructions rapidement

## 📝 Notes

- Les registres affichés : A, B, D, X, Y, U, S, PC, DP, CC
- La mémoire peut être navigée avec les boutons "Haut"/"Bas"
- Les breakpoints peuvent être ajoutés pour interrompre RUN
