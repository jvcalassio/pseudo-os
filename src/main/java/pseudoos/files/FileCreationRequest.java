package files;

public class FileCreationRequest {

    private final String fileName;
    private final FileData fileData;

    public FileCreationRequest(final String fileName,
                               final FileData fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public FileData getFileData() {
        return fileData;
    }
}
