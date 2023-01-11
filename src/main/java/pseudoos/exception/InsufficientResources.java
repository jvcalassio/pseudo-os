package exception;

public class InsufficientResources extends RuntimeException {

    public InsufficientResources(String resource) {
        super("Recursos insuficientes: " + resource);
    }
}
