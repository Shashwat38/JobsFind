package com.jobsfindai.backend.exception;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getStatusCode().value(), exception.getReason());

        return ResponseEntity.status(exception.getStatusCode()).body(errorResponse);
    }
}
