package exception;

public class NotFileOwnerException extends Exception {

    public NotFileOwnerException(String fileName, Integer PID) {
        super("Processo " + PID + " nao eh dono do arquivo " + fileName + ".");
    }
}
