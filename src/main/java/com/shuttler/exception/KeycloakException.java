package com.shuttler.exception;

import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {

    private int httpStatus;
    private String message;

    public KeycloakException(String message, Throwable err) {
        super(message, err);
    }

    public KeycloakException(String message, int httpStatus, Throwable err) {
        super(message, err);
        this.httpStatus = httpStatus;
    }

    public KeycloakException(String message, int httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
