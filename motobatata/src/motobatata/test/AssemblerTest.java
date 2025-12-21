package motobatata.test;

import motobatata.assembler.Assembler;
import motobatata.cpu.CPU;
import motobatata.memory.Memory;

/**
 * Test de vérification de l'assemblage et exécution
 */
public class AssemblerTest {

    public static void main(String[] args) {
        System.out.println("=== TEST ASSEMBLER ===\n");
        
        // Test 1 : LDA #$12
        testInstruction(new String[]{
            "LDA #$12"
        }, "LDA #$12");
        
        // Test 2 : LDB #$34
        testInstruction(new String[]{
            "LDB #$34"
        }, "LDB #$34");
        
        // Test 3 : LDX #$1234
        testInstruction(new String[]{
            "LDX #$1234"
        }, "LDX #$1234");
        
        // Test 4 : Combiné
        testInstruction(new String[]{
            "LDA #$12",
            "LDB #$34",
            "LDX #$5678",
            "LDY #$9ABC"
        }, "COMBINÉ: LDA, LDB, LDX, LDY");
    }
    
    static void testInstruction(String[] code, String description) {
        System.out.println("### TEST: " + description);
        System.out.println("Code:");
        for (String line : code) {
            System.out.println("  " + line);
        }
        System.out.println();
        
        try {
            // Assembler
            Assembler assembler = new Assembler();
            byte[] bytecode = assembler.assemble(code);
            
            System.out.println("Bytecode généré (" + bytecode.length + " bytes):");
            for (int i = 0; i < bytecode.length; i++) {
                System.out.printf("  [%d] = 0x%02X\n", i, bytecode[i] & 0xFF);
            }
            
            // CPU
            Memory memory = new Memory();
            CPU cpu = new CPU(memory);
            
            // Load bytecode
            for (int i = 0; i < bytecode.length; i++) {
                memory.writeByte(i, bytecode[i]);
            }
            
            System.out.println("\nÉtat initial:");
            printRegisters(cpu);
            
            // Execute each instruction
            int instructionCount = 0;
            while (cpu.getRegPC() < bytecode.length && instructionCount < 10) {
                System.out.println("\nExécution instruction #" + (instructionCount + 1) + " à PC=0x" + 
                    String.format("%04X", cpu.getRegPC()));
                
                cpu.executeInstruction();
                instructionCount++;
                
                System.out.println("Après exécution:");
                printRegisters(cpu);
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "=".repeat(50) + "\n");
    }
    
    static void printRegisters(CPU cpu) {
        System.out.printf("  A=0x%02X  B=0x%02X  D=0x%04X\n", 
            cpu.getAccA(), cpu.getAccB(), cpu.getAccD());
        System.out.printf("  X=0x%04X  Y=0x%04X  U=0x%04X  S=0x%04X\n",
            cpu.getRegX(), cpu.getRegY(), cpu.getRegU(), cpu.getRegS());
        System.out.printf("  PC=0x%04X  DP=0x%02X  CC=0x%02X\n",
            cpu.getRegPC(), cpu.getRegDP(), cpu.getRegCC());
    }
}
