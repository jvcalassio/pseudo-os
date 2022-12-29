package exception;

public class InexistentFileException extends RuntimeException {

    public InexistentFileException() {
        super("Arquivo inexistente.");
    }
}
