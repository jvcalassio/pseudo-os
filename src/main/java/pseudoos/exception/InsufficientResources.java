package exception;

public class InsufficientResources extends RuntimeException {

    public InsufficientResources() {
        super("Recursos insuficientes.");
    }
}
