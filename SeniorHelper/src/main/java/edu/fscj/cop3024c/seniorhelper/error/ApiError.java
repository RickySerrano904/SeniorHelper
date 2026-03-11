package edu.fscj.cop3024c.seniorhelper.error;

import java.time.Instant;

public class ApiError {
    private final String path;
    private final String message;
    private final int status;
    private final Instant timestamp;

    public ApiError(String path, String message, int status) {
        this.path = path;
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public String getPath() { return path; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public Instant getTimestamp() { return timestamp; }}
