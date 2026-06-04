package com.openclassrooms.starterjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MailAlreadyExistsException extends RuntimeException {
    public MailAlreadyExistsException() {
        super("Email already exists");
    }
}
