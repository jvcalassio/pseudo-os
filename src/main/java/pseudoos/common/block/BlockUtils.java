package common.block;

import exception.NotEnoughMemoryException;
import memory.MemoryManager;
import util.Logger;

import java.util.List;
import java.util.Optional;
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

    public static void freeBlocks(final int start,
                                  final int end,
                                  final List<Block> targetBlockList) {
        targetBlockList.subList(start, end).forEach(Block::free);
    }

    public static Optional<Integer> firstFit(List<Block> blockList, int size) {
        // verificar se tem espaco continuo de tamanho SIZE na lista recebida
        int firstFreeBlock = -1;
        int possibleInitialPosition = -1;

        for(Block blk : blockList){
            if(!blk.isUsed()){
                if(firstFreeBlock == -1){
                    firstFreeBlock = blockList.indexOf(blk);
                    possibleInitialPosition = firstFreeBlock;
                }
                if(blockList.indexOf(blk) == possibleInitialPosition + size - 1){
                    // se tiver, verifica e, se estiver tudo vazio, alocar e retornar o numero do bloco em que comeca
                    boolean allFree = true;
                    for(int i = firstFreeBlock; i < firstFreeBlock + size - 1; i++){
                        if(blockList.get(i).isUsed()){
                            allFree = false;
                            break;
                        }
                    }
                    if(allFree) {
                        for (int i = firstFreeBlock; i < firstFreeBlock + size; i++) {
                            blockList.get(i).alloc((int) (Math.random() * 1000));
                        }
                        return Optional.of(firstFreeBlock);
                    }
                }
            }else{
                firstFreeBlock = -1;
                possibleInitialPosition = -1;
            }
        }
        return Optional.empty();
    }

}
