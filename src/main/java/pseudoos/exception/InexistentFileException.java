package exception;

public class InexistentFileException extends RuntimeException {

    public InexistentFileException(String fileName) {
        super("Arquivo " + fileName + " inexistente.");
    }
}
