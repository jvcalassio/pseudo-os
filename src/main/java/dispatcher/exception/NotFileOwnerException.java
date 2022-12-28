package exception;

public class NotFileOwnerException extends RuntimeException {

    public NotFileOwnerException() {
        super("Processo nao eh dono do arquivo.");
    }
}
