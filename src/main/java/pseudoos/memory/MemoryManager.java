package memory;

import common.block.Block;
import common.block.BlockUtils;
import exception.NotEnoughMemoryException;
import util.Logger;

import java.util.List;

public class MemoryManager {

    private static MemoryManager instance;

    private final List<Block> memory;

    public static MemoryManager getInstance(){
        if (instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }

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
        List<Block> realTimeBlocks = getRealTimeBlocks();

        int firstFreeBlock = -1;
        int possibleInitialPosition = -1;

        for(Block blk : realTimeBlocks){
            if(!blk.isUsed()){
                if(firstFreeBlock == -1){
                    firstFreeBlock = realTimeBlocks.indexOf(blk);
                    possibleInitialPosition = firstFreeBlock;
                }
                if(realTimeBlocks.indexOf(blk) == possibleInitialPosition + size - 1){
                    // se tiver, alocar e retornar o numero do bloco em que comeca
                    Logger.info("Alocando memória real-time no blocos [" + firstFreeBlock + ":" + (firstFreeBlock + size) + "]");

                    for(int i = firstFreeBlock; i < firstFreeBlock + size; i++){
                        getRealTimeBlocks().get(i).alloc((int) (Math.random() * 1000));
                    }

                    return firstFreeBlock;
                }
            }
        }
        Logger.info("Não há memória real-time suficiente para alocar " + size + " blocos");
        throw new NotEnoughMemoryException();
    }

    public int allocateUserBlocks(final int size) {
        // verificar se tem espaco continuo de tamanho SIZE em getRealTimeBlocks()
        List<Block> realTimeBlocks = getUserBlocks();

        int firstFreeBlock = -1;
        int possibleInitialPosition = -1;

        for(Block blk : realTimeBlocks){
            if(!blk.isUsed()){
                if(firstFreeBlock == -1){
                    firstFreeBlock = realTimeBlocks.indexOf(blk);
                    possibleInitialPosition = firstFreeBlock;
                }
                if(realTimeBlocks.indexOf(blk) == possibleInitialPosition + size){
                    // se tiver, alocar e retornar o numero do bloco em que comeca
                    Logger.info("Alocando memória de usuário no blocos [" + firstFreeBlock + ":" + (firstFreeBlock + size) + "]");

                    for(int i = firstFreeBlock; i < firstFreeBlock + size; i++){
                        getUserBlocks().get(i).alloc((int) (Math.random() * 1000));
                    }

                    return firstFreeBlock;
                }
            }
        }
        Logger.info("Não há memória de usuário suficiente para alocar " + size + " blocos");
        throw new NotEnoughMemoryException();
    }

    public void freeRealTimeBlocks(final int initialBlock, final int size) {
        Logger.info("Liberando memória real-time nos blocos [" + initialBlock + ":" + (initialBlock + size) + "]");
        for(int i = initialBlock; i < initialBlock + size; i++){
            getRealTimeBlocks().get(i).free();
        }
    }

    public void freeUserBlocks(final int initialBlock, final int size) {
        Logger.info("Liberando memória de usuário nos blocos [" + initialBlock + ":" + (initialBlock + size) + "]");
        for(int i = initialBlock; i < initialBlock + size; i++){
            getUserBlocks().get(i).free();
        }
    }
}
