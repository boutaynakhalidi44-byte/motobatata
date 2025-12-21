package motobatata.instructions;

import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * Interface représentant une instruction du processeur Motorola 6809
 */
public interface Instruction {
    
    /**
     * Exécute l'instruction courante.
     * @param cpu Le processeur
     * @param memory La mémoire
     */
    void execute(CPU cpu, Memory memory);
    
    /**
     * Retourne le mnémonique de l'instruction
     * @return Le mnémonique
     */
    String getMnemonic();
    
    /**
     * Retourne la taille de l'instruction en octets
     * @return La taille
     */
    int getSize();
}

