package ru.cft.template.core.exception;

public class BadTransactionException extends RuntimeException {
    public BadTransactionException(String message) {
        super(message);
    }
}
