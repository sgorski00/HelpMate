package pl.sgorski.common.exception;

public class IllegalStatusChangeException extends RuntimeException{

    public IllegalStatusChangeException(String message) {
        super(message);
    }
}
