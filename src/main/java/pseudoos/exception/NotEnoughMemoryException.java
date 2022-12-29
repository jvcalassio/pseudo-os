package exception;

public class NotEnoughMemoryException extends RuntimeException {

    public NotEnoughMemoryException() {
        super("Sem espaco suficiente em memoria.");
    }
}
