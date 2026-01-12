package net.springprojectbackend.springboot.service;

public class AgentAuthException extends RuntimeException {
    AgentAuthException(String message) {
        super(message);
    }
}