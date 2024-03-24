package com.oxygen.task.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericException extends RuntimeException{
    private final HttpStatus status;

    public GenericException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static GenericException badRequest(String message) {
        return new GenericException(message, HttpStatus.BAD_REQUEST);
    }
}
