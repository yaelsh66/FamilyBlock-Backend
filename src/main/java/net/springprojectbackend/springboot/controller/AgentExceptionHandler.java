package net.springprojectbackend.springboot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import net.springprojectbackend.springboot.service.AgentAuthException;

@ControllerAdvice
public class AgentExceptionHandler {

    /**
     * Authentication / authorization failures
     */
    @ExceptionHandler(AgentAuthException.class)
    public ResponseEntity<String> handleAuthException(AgentAuthException ex) {
        // Do NOT leak sensitive details
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized");
    }

    /**
     * Bad input from agent (JSON, missing fields, etc.)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bad request");
    }

    /**
     * Catch-all for unexpected backend errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // IMPORTANT:
        // Log full stack trace internally
        ex.printStackTrace();

        // But return generic error to agent
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }
}
