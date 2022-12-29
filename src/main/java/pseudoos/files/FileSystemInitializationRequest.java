package files;

import processes.ProcessInstruction;

import java.util.List;

public class FileSystemInitializationRequest {

    private final int totalBlocks;
    private final List<FileCreationRequest> initialFileSystem;
    private final List<ProcessInstruction> instructions;

    public FileSystemInitializationRequest(final int totalBlocks,
                                           final List<FileCreationRequest> initialFileSystem,
                                           final List<ProcessInstruction> instructions) {
        this.totalBlocks = totalBlocks;
        this.initialFileSystem = initialFileSystem;
        this.instructions = instructions;
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public List<FileCreationRequest> getInitialFileSystem() {
        return initialFileSystem;
    }

    public List<ProcessInstruction> getInstructions() {
        return instructions;
    }

}
