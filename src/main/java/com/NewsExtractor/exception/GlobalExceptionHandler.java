package com.NewsExtractor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {SpecificException.class})
    public ResponseEntity<String> handleNotFoundException(SpecificException specificException) {
        return ResponseEntity.status(specificException.getStatusCode())
                .body(specificException.getReason());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<HashMap<String, String>> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException exception) {
        HashMap<String, String> errors = new HashMap<>();
        exception.getFieldErrors().forEach(error -> errors.put(error.getField(),
                error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(value = {SocketException.class})
    public ResponseEntity<?> handleSocketException(SocketException exception) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<?> handleIOException(IOException exception) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(exception.getMessage());
    }

}
