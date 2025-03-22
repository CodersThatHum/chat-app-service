package com.codersthathum.chat_app_service.controller;

import com.codersthathum.chat_app_service.dto.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HttpResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

}
