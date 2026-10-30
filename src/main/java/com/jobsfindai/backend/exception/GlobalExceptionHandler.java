package com.jobsfindai.backend.exception;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getStatusCode().value(), exception.getReason());

        return ResponseEntity.status(exception.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException exception) {
        ErrorResponse errorResponse = new ErrorResponse(400, "Unable to read PDF file");

        return ResponseEntity.status(400).body(errorResponse);
    }


}
