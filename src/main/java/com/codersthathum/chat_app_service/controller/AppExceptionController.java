package com.codersthathum.chat_app_service.controller;

import com.codersthathum.chat_app_service.dto.http.HttpResponse;
import com.codersthathum.chat_app_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionController {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<HttpResponse<Void>> handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(HttpResponse.error(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HttpResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(HttpResponse.error(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<HttpResponse<Void>> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(HttpResponse.error(request, HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<HttpResponse<Void>> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(HttpResponse.error(request, HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<HttpResponse<Void>> handleTooManyRequestException(TooManyRequestException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(HttpResponse.error(request, HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
    }

}
