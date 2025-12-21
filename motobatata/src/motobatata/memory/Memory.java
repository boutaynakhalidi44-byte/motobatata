package motobatata.memory;

/**
 * Représente la mémoire du processeur Motorola 6809.
 * Capacité: 64 Ko (0x0000 - 0xFFFF)
 */
public class Memory {

    public static final int MEMORY_SIZE = 0x10000; //65536 bytes en hexa
    private final byte[] memory;

    public Memory() {
        memory = new byte[MEMORY_SIZE];
        reset();
    }

    // Réinitialiser la mémoire
    public void reset() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = 0x00;
        }
    }

    // Lecture d'un octet (8 bits)
    public int readByte(int address) {
        checkAddress(address);
        return memory[address] & 0xFF;
    }

    // Écriture d'un octet (8 bits)
    public void writeByte(int address, int value) {
        checkAddress(address);
        memory[address] = (byte) (value & 0xFF);
    }

    // Lecture d'un mot (16 bits)
    public int readWord(int address) {
        int high = readByte(address);
        int low  = readByte(address + 1);
        return (high << 8) | low;
    }

    // Écriture d'un mot (16 bits)
    public void writeWord(int address, int value) {
        writeByte(address, (value >> 8) & 0xFF);
        writeByte(address + 1, value & 0xFF);
    }

    // Charger un programme en mémoire
    public void loadProgram(byte[] program, int startAddress) {
        for (int i = 0; i < program.length; i++) {
            writeByte(startAddress + i, program[i]);
        }
    }

    // Vérification d'adresse
    private void checkAddress(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new IllegalArgumentException(
                String.format("Adresse mémoire invalide : 0x%04X", address)
            );
        }
    }
}

