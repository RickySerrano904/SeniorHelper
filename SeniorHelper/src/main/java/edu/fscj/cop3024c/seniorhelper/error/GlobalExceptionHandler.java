package edu.fscj.cop3024c.seniorhelper.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> nf(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(req.getRequestURI(), ex.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> val(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField()+": "+e.getDefaultMessage())
                .findFirst().orElse("Validation error");
        logger.warn("Validation failed: {}", String.join(", ", msg));
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), msg, 400));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> cons(ConstraintViolationException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), ex.getMessage(), 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(req.getRequestURI(), ex.getMessage(), 500));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiError> auth(SecurityException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(req.getRequestURI(), ex.getMessage(), 401));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> badJson(org.springframework.http.converter.HttpMessageNotReadableException ex,
                                            HttpServletRequest req) {
        return ResponseEntity.badRequest()
                .body(new ApiError(req.getRequestURI(), "Malformed JSON request", 400));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> db(org.springframework.dao.DataIntegrityViolationException ex,
                                       HttpServletRequest req) {
        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        return ResponseEntity.badRequest()
                .body(new ApiError(req.getRequestURI(),
                        "Invalid data violates database constraints: " + detail, 400));
    }
}

