package com.mercari.processor;


public class IllegalDeclarationException extends RuntimeException{
    public IllegalDeclarationException() {
    }

    public IllegalDeclarationException(String message) {
        super(message);
    }

    public IllegalDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDeclarationException(Throwable cause) {
        super(cause);
    }
}
