package files;

import common.block.Block;
import common.block.BlockUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileManager {

    private int totalBlocks;
    private List<Block> fileSystem;
    private final ConcurrentMap<String, FileData> fileMap;
    private List<FileInstruction> instructions;

    public FileManager() {
        this.totalBlocks = 0;
        this.fileMap = new ConcurrentHashMap<>();
        this.instructions = new LinkedList<>();
        this.fileSystem = new LinkedList<>();
    }

    public void initialize(final int numberOfBlocks,
                           final List<FileCreationRequest> fileCreationRequests,
                           final List<FileInstruction> instructions) {
        this.totalBlocks = numberOfBlocks;
        this.instructions = instructions;
        this.fileSystem = BlockUtils.generateEmptyBlocks(this.totalBlocks);
        fileCreationRequests.forEach(this::createFile);

        fileMap.forEach((key, value) ->
                BlockUtils.allocateBlocks(value.getStartingPosition(),
                        value.getStartingPosition() + value.getSize(), this.fileSystem)
        );
    }

    private int allocateDiskBlocks(final int size) {
        // verificar se tem espaco continuo de tamanho SIZE em fileSystem
        // se tiver, alocar e retornar o numero do bloco em que comeca
        // se nao tiver, throw new NotEnoughDiskException
        // lembrandop que precisa verificar usando o first-fit
        return 0;
    }

    public void createFile(final FileCreationRequest fileCreationRequest) {
        if (fileCreationRequest.getFileData().getOwnedBy() == FileOwnedBy.SYSTEM) {
            // criado pelo sistema, deve tomar posicao inicial
            this.fileMap.put(fileCreationRequest.getFileName(), fileCreationRequest.getFileData());
        } else {
            final int start = allocateDiskBlocks(fileCreationRequest.getFileData().getSize());
            this.fileMap.put(fileCreationRequest.getFileName(),
                    new FileData(
                            start,
                            fileCreationRequest.getFileData().getSize(),
                            fileCreationRequest.getFileData().getOwnedBy(),
                            fileCreationRequest.getFileData().getOwnerPID()
                    )
            );
        }
    }

    public void deleteFile(final String fileName) {
        // remover arquivo do fileSystem
        // verificar se arquivo existe, se nao existe throw new InexistentFileException()
        // verificar se o dono eh quem pediu pra deletar (se n for prio = 0), se n for NotFileOwnerException
    }

}
