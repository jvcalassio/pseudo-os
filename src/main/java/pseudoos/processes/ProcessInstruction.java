package processes;

import files.FileOperation;

public class ProcessInstruction {

    private final int PID;
    private final FileOperation operation;
    private final String fileName;
    private final Integer fileSize;

    public ProcessInstruction(int PID, FileOperation operation, String fileName, int fileSize) {
        this.PID = PID;
        this.operation = operation;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public ProcessInstruction(int PID, FileOperation operation, String fileName) {
        this.PID = PID;
        this.operation = operation;
        this.fileName = fileName;
        this.fileSize = null;
    }

}
