package com.example.rival.exception;

// Thrown when a rival request breaks a rule. The message is safe to show to the user.
public class RivalRequestException extends RuntimeException {

    public RivalRequestException(String message) {
        super(message);
    }
}
