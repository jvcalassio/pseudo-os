package memory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemoryManager {

    private final List<Block> memory;

    public MemoryManager() {
        this.memory = IntStream.range(0, 1024).mapToObj(num -> new Block()).collect(Collectors.toList());
    }

    public List<Block> getRealTimeBlocks() {
        return memory.subList(0, 64);
    }

    public List<Block> getUserBlocks() {
        return memory.subList(64, 1024);
    }
}
