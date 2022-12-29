package common.block;

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

    // provavelmente implementar aqui algoritmo de encontrar first-fit

    // provavelmente implementar aqui algoritmo de encontrar best-fit

}
