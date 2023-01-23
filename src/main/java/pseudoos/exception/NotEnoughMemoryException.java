package exception;

public class NotEnoughMemoryException extends RuntimeException {

    public NotEnoughMemoryException() {
        super("Sem espaco suficiente em memoria.");
    }

    public NotEnoughMemoryException(Integer PID, RuntimeException e) {
        super("Processo " + PID + " não foi criado por falta de espaço na memória.", e);
    }

}
