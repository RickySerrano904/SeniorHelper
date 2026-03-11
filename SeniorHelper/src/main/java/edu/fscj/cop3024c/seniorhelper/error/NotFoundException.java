package edu.fscj.cop3024c.seniorhelper.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Integer id) {
    super("Resource not found with ID: " + id);
}
    public NotFoundException(String message) { super(message); }
}
