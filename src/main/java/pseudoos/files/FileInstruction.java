package files;

import common.FileOperation;

public class FileInstruction {

    private final int PID;
    private final FileOperation operation;
    private final String fileName;
    private final Integer fileSize;

    public FileInstruction(int PID, FileOperation operation, String fileName, int fileSize) {
        this.PID = PID;
        this.operation = operation;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public FileInstruction(int PID, FileOperation operation, String fileName) {
        this.PID = PID;
        this.operation = operation;
        this.fileName = fileName;
        this.fileSize = null;
    }

}
