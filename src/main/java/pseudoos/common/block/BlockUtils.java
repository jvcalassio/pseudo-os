package common.block;

import exception.NotEnoughMemoryException;
import memory.MemoryManager;
import util.Logger;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockUtils {

    public static List<Block> generateEmptyBlocks(final int number) {
        return IntStream.range(0, number).mapToObj(num -> new Block()).collect(Collectors.toUnmodifiableList());
    }

    public static void allocateBlocks(final int start,
                                            final int end,
                                            final List<Block> targetBlockList) {
        final Random random = new Random();
        targetBlockList.subList(start, end).forEach(block -> block.alloc(random.nextInt()));
    }

    public static int firstFit(List<Block> blockList, int size){
        // verificar se tem espaco continuo de tamanho SIZE em getRealTimeBlocks()
        int firstFreeBlock = -1;
        int possibleInitialPosition = -1;

        for(Block blk : blockList){
            if(!blk.isUsed()){
                if(firstFreeBlock == -1){
                    firstFreeBlock = blockList.indexOf(blk);
                    possibleInitialPosition = firstFreeBlock;
                }
                if(blockList.indexOf(blk) == possibleInitialPosition + size - 1){
                    // se tiver, alocar e retornar o numero do bloco em que comeca
                    Logger.info("Alocando memória real-time no blocos [" + firstFreeBlock + ":" + (firstFreeBlock + size) + "]");

                    for(int i = firstFreeBlock; i < firstFreeBlock + size; i++){
                        MemoryManager.getInstance().getRealTimeBlocks().get(i).alloc((int) (Math.random() * 1000));
                    }

                    return firstFreeBlock;
                }
            }
        }
        Logger.info("Não há memória real-time suficiente para alocar " + size + " blocos");
        throw new NotEnoughMemoryException();
    }

}
