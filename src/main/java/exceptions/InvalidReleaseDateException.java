package exceptions;

public class InvalidReleaseDateException extends RuntimeException {
    public InvalidReleaseDateException(String message) {
        super(message);
    }
}
