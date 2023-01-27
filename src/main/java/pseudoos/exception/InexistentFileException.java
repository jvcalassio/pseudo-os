package exception;

public class InexistentFileException extends Exception {

    public InexistentFileException(String fileName) {
        super("Arquivo " + fileName + " inexistente.");
    }
}
