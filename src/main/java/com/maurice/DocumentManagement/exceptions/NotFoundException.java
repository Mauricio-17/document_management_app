package com.maurice.DocumentManagement.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1;

    public NotFoundException(String message){
        super(message);
    }
    public NotFoundException(String message, Throwable tr){
        super(message, tr);
    }

}
