package exception;

public class NotEnoughDiskException extends RuntimeException {

    public NotEnoughDiskException() {
        super("Sem espaco suficiente em disco.");
    }

}
