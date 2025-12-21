; ═══════════════════════════════════════════════════════════════════
; EXEMPLE DE CODE ASSEMBLEUR - Simulateur Motorola 6809
; ═══════════════════════════════════════════════════════════════════

; Exemple 1: Addition simple
; Ajoute 0x05 + 0x03 et stocke en mémoire à 0x0100

        LDA #$05        ; Charger 0x05 dans A
        ADDA #$03       ; A = A + 0x03 = 0x08
        STA $0100       ; Stocker A à l'adresse 0x0100
        BRA *           ; Boucle infinie


; ═══════════════════════════════════════════════════════════════════
; Exemple 2: Boucle avec compteur
; Compte de 0 à 16 et stocke le résultat
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDD #$0000      ; D = 0 (compteur)

LOOP:   ADDD #$0001     ; D++ (incrémenter)
        CMPD #$0010     ; Comparer D avec 16
        BNE LOOP        ; Si différent, continuer la boucle
        
        STD $0100       ; Stocker le résultat (D = 16) à 0x0100
        BRA *           ; Fin


; ═══════════════════════════════════════════════════════════════════
; Exemple 3: Appel de fonction (Subroutine)
; ═══════════════════════════════════════════════════════════════════

        ORG $0000

START:  LDA #$0A        ; A = 10
        JSR ADD5        ; Appeler ADD5
        STA $0100       ; Stocker résultat
        BRA *           ; Fin


ADD5:   ADDA #$05       ; A = A + 5
        RTS             ; Retour


; ═══════════════════════════════════════════════════════════════════
; Exemple 4: Accès à la mémoire
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDA #$42        ; A = 0x42 ('B')
        STA $0200       ; Stocker 'B' à 0x0200
        
        LDB #$4F        ; B = 0x4F ('O')
        STB $0201       ; Stocker 'O' à 0x0201
        
        LDA $0200       ; Charger depuis 0x0200
        BRA *


; ═══════════════════════════════════════════════════════════════════
; Exemple 5: Opérations logiques
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDA #$F0        ; A = 11110000
        ANDA #$0F       ; A = A AND 00001111 = 00000000
        
        LDB #$AA        ; B = 10101010
        ORB #$55        ; B = B OR 01010101 = 11111111
        
        EORA #$FF       ; A = A XOR 11111111 = 11111111
        
        STA $0100       ; Stocker résultat
        BRA *


; ═══════════════════════════════════════════════════════════════════
; Exemple 6: Décalages et rotations
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDA #$80        ; A = 10000000
        LSRA            ; A = 01000000 (décalage logique droit)
        LSRA            ; A = 00100000
        
        LDB #$01        ; B = 00000001
        LSLB            ; B = 00000010 (décalage logique gauche)
        LSLB            ; B = 00000100
        
        STA $0100       ; Stocker A
        STB $0101       ; Stocker B
        BRA *


; ═══════════════════════════════════════════════════════════════════
; Exemple 7: Branchements conditionnels
; Teste différents flags
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDA #$00        ; A = 0 (Z flag = 1)
        
        BEQ ZERO        ; Si A = 0, sauter à ZERO
        LDA #$FF        ; Sinon, A = 0xFF
        BRA DONE
        
ZERO:   LDA #$42        ; A = 0x42

DONE:   STA $0100       ; Stocker
        BRA *


; ═══════════════════════════════════════════════════════════════════
; Exemple 8: Registres d'index
; Accès au mémoire avec indexation
; ═══════════════════════════════════════════════════════════════════

        ORG $0000
        
        LDX #$0100      ; X = 0x0100 (adresse de base)
        
        LDA #$11        ; A = 0x11
        STA ,X          ; Stocker à [X] = 0x0100
        
        LDA #$22        ; A = 0x22
        STA 1,X         ; Stocker à [X+1] = 0x0101
        
        LDA #$33        ; A = 0x33
        STA 2,X         ; Stocker à [X+2] = 0x0102
        
        BRA *


; ═══════════════════════════════════════════════════════════════════
; INSTRUCTIONS DE GUIDE D'UTILISATION DU SIMULATEUR
; ═══════════════════════════════════════════════════════════════════

; 1. COPIER l'un des exemples ci-dessus
; 2. COLLER dans l'éditeur de code du simulateur
; 3. Cliquer STEP pour exécuter instruction par instruction
;    OU cliquer RUN pour exécution continue
; 4. OBSERVER les changements dans:
;    - Registres (A, B, X, Y, D, PC, CC)
;    - Mémoire (adresses 0x0100-0x0102)
;    - Flags (N, Z, V, C, H)

; MODES D'ADRESSAGE:
;    #$XX        - Immédiat 8-bit (direct value)
;    <$XX        - Direct (page zéro)
;    $XXXX       - Étendu (16-bit address)
;    ,X / ,Y     - Indexé (indirect register)
;    n,X / n,Y   - Indexé avec offset
;    LABEL       - Étiquette (auto-calculé)

; DRAPEAUX (CC register):
;    Z (bit 1) - Zero flag (résultat = 0)
;    N (bit 2) - Negative flag (bit 7 = 1)
;    V (bit 1) - Overflow flag
;    C (bit 0) - Carry flag

; ═══════════════════════════════════════════════════════════════════
