package com.abaduna.microservicioEventos.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        if (e.getMessage().contains("Usuario no válido")) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } else if (e.getMessage().contains("No autorizado")) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } else if (e.getMessage().contains("no encontrada")) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } else {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}