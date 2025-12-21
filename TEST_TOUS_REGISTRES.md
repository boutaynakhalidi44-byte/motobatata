# Test Complet des Registres - Vérification de Mise à Jour

## 🧪 Test 1 : Registres A et B

### Code Assembleur
```asm
LDA #$12
LDB #$34
```

### Résultat Attendu
```
A = 0x12 ✓
B = 0x34 ✓
PC avance normalement
```

### Procédure
1. Entrez le code ci-dessus
2. Cliquez **COMPILER**
3. Cliquez **STEP** (1ère fois)
   - ✅ A doit devenir 0x12
   - ✅ B doit rester 0x00
   - ✅ PC doit devenir 0x0001 ou 0x0002
4. Cliquez **STEP** (2e fois)
   - ✅ A doit rester 0x12
   - ✅ B doit devenir 0x34
   - ✅ PC doit avancer

---

## 🧪 Test 2 : Registres 16-bit (X, Y)

### Code Assembleur
```asm
LDX #$1234
LDY #$5678
```

### Résultat Attendu
```
X = 0x1234 ✓
Y = 0x5678 ✓
```

### Procédure
1. Compilez
2. Cliquez **STEP** (1ère fois)
   - ✅ X doit devenir 0x1234
3. Cliquez **STEP** (2e fois)
   - ✅ Y doit devenir 0x5678

---

## 🧪 Test 3 : Accumulteur D (A:B combiné)

### Code Assembleur
```asm
LDA #$12
LDB #$34
; D devrait être 0x1234
```

### Résultat Attendu
```
D = 0x1234 (combinaison de A et B)
```

### Procédure
1. Compilez et exécutez les deux instructions
2. Vérifiez que **D = 0x1234** (12 suivi de 34)
3. Si D affiche 0x1234, c'est correct ✓

---

## 🧪 Test 4 : PC (Program Counter)

### Code Assembleur
```asm
LDA #$FF
ADDA #$01
STA $100
```

### Résultat Attendu
```
PC avant : 0x0000
PC après LDA : 0x0002 (opcode 1 byte + operand 1 byte)
PC après ADDA : 0x0004
PC après STA : 0x0007 ou 0x0008
```

### Procédure
1. Compilez
2. Cliquez **STEP** plusieurs fois
3. Vérifiez que PC avance correctement

---

## 🧪 Test 5 : Flags (CC Register)

### Code Assembleur
```asm
LDA #$00
; Z flag devrait être 1 (A est zéro)
```

### Résultat Attendu
```
CC = 0x04 (Z flag set)
```

### Procédure
1. Compilez
2. Cliquez **STEP**
3. Vérifiez que **CC = 0x04**

---

## 🧪 Test 6 : Test Complet (Tous les Registres)

### Code Assembleur
```asm
LDA #$11      ; A = 0x11
LDB #$22      ; B = 0x22
LDX #$3333    ; X = 0x3333
LDY #$4444    ; Y = 0x4444
ADDA #$11     ; A = 0x11 + 0x11 = 0x22
ADDB #$22     ; B = 0x22 + 0x22 = 0x44
```

### Résultat Attendu
```
A = 0x22
B = 0x44
D = 0x2244
X = 0x3333
Y = 0x4444
PC avance
```

### Procédure
1. Compilez
2. Cliquez **RUN**
3. Attendez 0.6-1 secondes
4. Vérifiez tous les registres affichent les bonnes valeurs

---

## ✅ Checklist de Vérification

- [ ] A se met à jour correctement
- [ ] B se met à jour correctement  
- [ ] D affiche la combinaison correcte de A:B
- [ ] X se met à jour correctement
- [ ] Y se met à jour correctement
- [ ] U se met à jour correctement
- [ ] S se met à jour correctement
- [ ] PC avance à chaque instruction
- [ ] DP se met à jour
- [ ] CC (Flags) se mettent à jour

---

## 🔍 Dépannage

### Si un registre ne se met pas à jour :

1. **Vérifiez l'assemblage** : Le bytecode a-t-il le bon nombre d'octets ?
2. **Vérifiez le PC** : Avance-t-il correctement ?
3. **Vérifiez la mémoire** : Les octets sont-ils chargés correctement ?
4. **Vérifiez l'instruction** : La classe d'instruction appelle-t-elle `cpu.setAccA()`, `cpu.setAccB()`, etc. ?

### Commandes de debug :

Vous pouvez ajouter des print statements dans les classes d'instructions pour vérifier que `execute()` est appelé.

---

## 📝 Notes

- Chaque instruction modifie certains registres
- Les flags (CC) changent aussi
- Le PC doit TOUJOURS avancer
- La mémoire doit être correctement mise à jour
