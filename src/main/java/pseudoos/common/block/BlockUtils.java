package common.block;

import java.util.BitSet;
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
                                      final List<Block> targetBlockList,
                                      final BitSet targetBitSet) {
        final Random random = new Random();
        targetBlockList.subList(start, end).forEach(block -> block.alloc(random.nextInt()));
        targetBitSet.set(start, end);
    }

    public static void freeBlocks(final int start,
                                  final int end,
                                  final List<Block> targetBlockList,
                                  final BitSet targetBitSet) {
        targetBlockList.subList(start, end).forEach(Block::free);
        targetBitSet.clear(start, end);
    }

    public static Optional<Integer> firstFit(final BitSet allocationMap, int allocationSize) {
        int startingBlock = 0;
        boolean fit = false;
        final BitSet mask = new BitSet(allocationSize);
        for (int i = 0; i <= allocationMap.size() - allocationSize; i++) {
            final BitSet candidateBlocks = allocationMap.get(startingBlock, startingBlock + allocationSize);
            candidateBlocks.or(mask);
            if (candidateBlocks.equals(mask)) {
                fit = true;
                break;
            }
            startingBlock++;
        }

        if (fit) {
            return Optional.of(startingBlock);
        }
        return Optional.empty();
    }

}
