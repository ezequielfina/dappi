package com.uade.tg.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resource) {
        super("The submitted resource " + resource + " was not found");
    }
}
