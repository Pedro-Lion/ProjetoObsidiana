package com.example.crudObsidiana.exception;

import java.time.LocalDateTime;

public class ErroApi {
    private final LocalDateTime timestamp;
    private final String message;

    public ErroApi(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
}