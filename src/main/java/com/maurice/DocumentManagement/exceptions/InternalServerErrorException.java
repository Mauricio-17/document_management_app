package com.maurice.DocumentManagement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1;

    public InternalServerErrorException(String message) {
        super(message);
    }
}
