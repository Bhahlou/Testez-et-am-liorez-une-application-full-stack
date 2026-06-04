package com.openclassrooms.starterjwt.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormat() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MailAlreadyExistsException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.badRequest().build();
    }
    
}
