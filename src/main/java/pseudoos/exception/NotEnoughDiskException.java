package exception;

public class NotEnoughDiskException extends Exception {

    public NotEnoughDiskException() {
        super("Sem espaco suficiente em disco.");
    }

}
