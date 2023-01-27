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

    public List<Block> getRealTimeBlocks() { return memory.subList(0, 64); }

    public List<Block> getUserBlocks() {
        return memory.subList(64, 1024);
    }


    public int allocateRealTimeBlocks(final int size) throws NotEnoughMemoryException {
        return BlockUtils.firstFit(getRealTimeBlocks(), size).orElseThrow(NotEnoughMemoryException::new);
    }

    public int allocateUserBlocks(final int size) throws NotEnoughMemoryException {
        return BlockUtils.firstFit(getUserBlocks(), size).orElseThrow(NotEnoughMemoryException::new);
    }

    public void freeRealTimeBlocks(final int initialBlock, final int size) {
        Logger.debug("Liberando memória real-time nos blocos [" + initialBlock + ":" + (initialBlock + size) + "]");
        for(int i = initialBlock; i < initialBlock + size; i++){
            getRealTimeBlocks().get(i).free();
        }
    }

    public void freeUserBlocks(final int initialBlock, final int size) {
        Logger.debug("Liberando memória de usuário nos blocos [" + initialBlock + ":" + (initialBlock + size) + "]");
        for(int i = initialBlock; i < initialBlock + size; i++){
            getUserBlocks().get(i).free();
        }
    }

}
