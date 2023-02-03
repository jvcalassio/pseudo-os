package memory;

import common.block.Block;
import common.block.BlockUtils;
import exception.NotEnoughMemoryException;
import util.Logger;

import java.util.BitSet;
import java.util.List;

public class MemoryManager {

    private static MemoryManager instance;

    private final List<Block> memory;
    private final BitSet allocationMap;

    public static MemoryManager getInstance(){
        if (instance == null) {
            instance = new MemoryManager();
        }
        return instance;
    }

    public MemoryManager() {
        this.memory = BlockUtils.generateEmptyBlocks(1024);
        this.allocationMap = new BitSet(1025);
        this.allocationMap.set(1024);
    }

    public BitSet getRealTimeAllocationMap() {
        return allocationMap.get(0, 64);
    }

    public BitSet getUserAllocationMap() {
        return allocationMap.get(64, 1024);
    }


    public int allocateRealTimeBlocks(final int size) throws NotEnoughMemoryException {
        final int startingBlock = BlockUtils.firstFit(getRealTimeAllocationMap(), size)
                                            .orElseThrow(NotEnoughMemoryException::new);
        final int endingBlock = startingBlock + size;

        if (startingBlock >= 0 && endingBlock <= 64) {
            BlockUtils.allocateBlocks(startingBlock, endingBlock, this.memory, this.allocationMap);
            return startingBlock;
        }
        throw new NotEnoughMemoryException();
    }

    public int allocateUserBlocks(final int size) throws NotEnoughMemoryException {
        final int startingBlock = BlockUtils.firstFit(getUserAllocationMap(), size)
                                            .orElseThrow(NotEnoughMemoryException::new) + 64;
        final int endingBlock = startingBlock + size + 64;

        if (startingBlock >= 64 && endingBlock <= 1024) {
            BlockUtils.allocateBlocks(startingBlock, endingBlock, this.memory, this.allocationMap);
            return startingBlock;
        }
        throw new NotEnoughMemoryException();
    }

    public void freeRealTimeBlocks(final int initialBlock, final int size) {
        Logger.debug("Liberando memória real-time nos blocos [" + initialBlock + ":" + (initialBlock + size - 1) + "]");
        BlockUtils.freeBlocks(initialBlock, initialBlock + size, this.memory, this.allocationMap);
    }

    public void freeUserBlocks(final int initialBlock, final int size) {
        Logger.debug("Liberando memória de usuário nos blocos [" + initialBlock + ":" + (initialBlock + size - 1) + "]");
        BlockUtils.freeBlocks(initialBlock + 64, initialBlock + size + 64, this.memory, this.allocationMap);
    }

}
