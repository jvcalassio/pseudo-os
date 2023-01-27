package files;

import common.FileOperation;
import common.block.Block;
import common.block.BlockUtils;
import exception.InexistentFileException;
import exception.NotEnoughDiskException;
import exception.NotFileOwnerException;
import processes.ProcessManager;
import util.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileManager {

    private static FileManager instance;
    private int totalBlocks;
    private List<Block> fileSystem;
    private final ConcurrentMap<String, FileData> fileMap;
    private final ConcurrentMap<Integer, List<FileInstruction>> instructionMap;

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private FileManager() {
        this.totalBlocks = 0;
        this.fileMap = new ConcurrentHashMap<>();
        this.fileSystem = new LinkedList<>();
        this.instructionMap = new ConcurrentHashMap<>();
    }

    public void initialize(final int numberOfBlocks,
                           final List<FileCreationRequest> fileCreationRequests,
                           final List<FileInstruction> instructions) throws NotEnoughDiskException {
        this.totalBlocks = numberOfBlocks;
        this.fileSystem = BlockUtils.generateEmptyBlocks(this.totalBlocks);
        for (FileCreationRequest fileCreationRequest : fileCreationRequests) {
            createFile(fileCreationRequest);
        }

        fileMap.forEach((key, value) ->
                BlockUtils.allocateBlocks(value.getStartingPosition(),
                        value.getStartingPosition() + value.getSize(), this.fileSystem)
        );

        instructions.forEach(instruction -> {
            if (!this.instructionMap.containsKey(instruction.getPID())) {
                this.instructionMap.put(instruction.getPID(), new LinkedList<>());
            }
            this.instructionMap.get(instruction.getPID()).add(instruction);
        });
    }

    private int allocateDiskBlocks(final int size) throws NotEnoughDiskException {
        return BlockUtils.firstFit(this.fileSystem, size).orElseThrow(NotEnoughDiskException::new);
    }

    public void processNextInstruction(final Integer PID) {
        if (!this.instructionMap.containsKey(PID)) {
            return;
        }

        final List<FileInstruction> instructions = this.instructionMap.get(PID);
        if (!instructions.isEmpty()) {
            this.processInstruction(instructions.remove(0));
        }
    }

    private void processInstruction(final FileInstruction fileInstruction) {
        if (fileInstruction.getOperation() == FileOperation.DELETE) {
            try {
                final Integer priority = ProcessManager.getInstance().getProcessList()
                        .get(fileInstruction.getPID()).getProcessPriority();
                deleteFile(fileInstruction.getFileName(), fileInstruction.getPID(), priority);

                final String message = MessageFormat.format(
                        "O processo {0} deletou o arquivo {1}",
                        fileInstruction.getPID(),
                        fileInstruction.getFileName()
                );

                Logger.info(message);
            } catch (NotFileOwnerException e) {
                final String message = MessageFormat.format(
                        "O processo {0} não pode apagar o arquivo {1} (nao eh dono)"
                        , fileInstruction.getPID(), fileInstruction.getFileName());
                Logger.info(message);
            } catch (InexistentFileException e) {
                final String message = MessageFormat.format(
                        "O processo {0} não pode apagar o arquivo {1} (inexistente)"
                        , fileInstruction.getPID(), fileInstruction.getFileName());
                Logger.info(message);
            }
        } else {
            try {
                final FileCreationRequest fileCreationRequest = new FileCreationRequest(
                        fileInstruction.getFileName(),
                        new FileData(
                                fileInstruction.getFileSize(),
                                mapByPID(fileInstruction.getPID()),
                                fileInstruction.getPID()
                        )
                );
                createFile(fileCreationRequest);

                final FileData fileData = fileMap.get(fileCreationRequest.getFileName());
                final String message = MessageFormat.format(
                        "O processo {0} criou o arquivo {1} (blocos {2}:{3})",
                        fileInstruction.getPID(),
                        fileInstruction.getFileName(),
                        fileData.getStartingPosition(),
                        fileData.getStartingPosition() + fileData.getSize()
                );

                Logger.info(message);
            } catch (NotEnoughDiskException e) {
                final String message = MessageFormat.format(
                        "O processo {0} não pode criar o arquivo {1} (falta de espaço)"
                , fileInstruction.getPID(), fileInstruction.getFileName());
                Logger.info(message);
            }
        }
    }

    private void createFile(final FileCreationRequest fileCreationRequest) throws NotEnoughDiskException {
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

    private void deleteFile(final String fileName, final Integer PID, final Integer priority)
            throws InexistentFileException, NotFileOwnerException {
        if (!fileMap.containsKey(fileName)) {
            throw new InexistentFileException(fileName);
        }

        if (priority != 0) {
            if (!Objects.equals(PID, fileMap.get(fileName).getOwnerPID())) {
                throw new NotFileOwnerException(fileName, PID);
            }
        }

        final FileData file = fileMap.get(fileName);

        BlockUtils.freeBlocks(file.getStartingPosition(), file.getStartingPosition() + file.getSize(), fileSystem);
        fileMap.remove(fileName);
    }

    private FileOwnedBy mapByPID(final Integer PID) {
        final Integer priority = ProcessManager.getInstance().getProcessList().get(PID).getProcessPriority();
        if (priority == 0) {
            return FileOwnedBy.REAL_TIME_PROCESS;
        }
        return FileOwnedBy.USER_PROCESS;
    }

    public void printAllocationMap() {
        final List<String> allocationMap = new ArrayList<>(Collections.nCopies(totalBlocks, " "));

        for (Map.Entry<String, FileData> entry : fileMap.entrySet()) {
            final int starting = entry.getValue().getStartingPosition();
            final int ending = entry.getValue().getStartingPosition() + entry.getValue().getSize();

            for (int i=starting;i<ending;i++) {
                allocationMap.set(i, entry.getKey());
            }
        }

        Formatter formatter = new Formatter();
        Formatter separator = new Formatter();
        allocationMap.forEach(item -> {
            separator.format(" ---");
            formatter.format("| %s ", item);
        });

        System.out.println(separator);
        System.out.println(formatter + "|");
        System.out.println(separator);
    }

}
