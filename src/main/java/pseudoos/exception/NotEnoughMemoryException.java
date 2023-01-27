package exception;

public class NotEnoughMemoryException extends Exception {

    private Integer PID;

    public NotEnoughMemoryException() {
        super("Sem espaco suficiente em memoria.");
    }

    public NotEnoughMemoryException(Integer PID, Exception e) {
        super("Processo " + PID + " não foi criado por falta de espaço na memória.", e);
        this.PID = PID;
    }

    public Integer getPID() {
        return this.PID;
    }

}
