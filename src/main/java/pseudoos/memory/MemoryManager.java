package memory;

import java.util.List;

public class MemoryManager {

    private final List<Block> memory;

    public MemoryManager() {
        this.memory = BlockUtils.generateEmptyBlocks(1024);
    }

    public List<Block> getRealTimeBlocks() {
        return memory.subList(0, 64);
    }

    public List<Block> getUserBlocks() {
        return memory.subList(64, 1024);
    }

    // escolher qual algoritmo adotar aqui pras alocacoes: first-first? best-fit?
    public int allocateRealTimeBlocks(final int size) {
        // verificar se tem espaco continuo de tamanho SIZE em getRealTimeBlocks()
        // se tiver, alocar e retornar o numero do bloco em que comeca
        // se nao tiver, throw new NotEnoughMemoryException
        return 0;
    }

    public int allocateUserBlocks(final int size) {
        // verificar se tem espaco continuo de tamanho SIZE em getUserBlocks()
        // se tiver, alocar e retornar o numero do bloco em que comeca
        // se nao tiver, throw new NotEnoughMemoryException
        return 0;
    }

}
