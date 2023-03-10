package inputreader.file;

import files.FileCreationRequest;
import files.FileInstruction;

import java.util.List;

public class FileSystemInitializationRequest {

    private final int totalBlocks;
    private final List<FileCreationRequest> initialFileSystem;
    private final List<FileInstruction> instructions;

    public FileSystemInitializationRequest(final int totalBlocks,
                                           final List<FileCreationRequest> initialFileSystem,
                                           final List<FileInstruction> instructions) {
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

    public List<FileInstruction> getInstructions() {
        return instructions;
    }

}
