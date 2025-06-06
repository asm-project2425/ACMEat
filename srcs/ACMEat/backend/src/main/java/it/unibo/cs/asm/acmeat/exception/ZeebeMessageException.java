package it.unibo.cs.asm.acmeat.exception;

public class ZeebeMessageException extends RuntimeException {
    public ZeebeMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
