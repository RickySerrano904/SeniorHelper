package edu.fscj.cop3024c.seniorhelper.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- 400 — BAD REQUEST ----------

    // @Valid on @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream().map(err -> err.getDefaultMessage()).findFirst().orElse("Validation error");

        log.warn("Validation failed: {}", msg);

        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), msg, 400));
    }

    // @Validated on query params / path params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), ex.getMessage(), 400));
    }

    // Malformed JSON, unreadable body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), "Malformed JSON request", 400));
    }

    // Illegally formatted input (enum parsing, role parsing, etc.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), ex.getMessage(), 400));
    }

    // DB constraint violations (FK, unique, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {

        String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return ResponseEntity.badRequest().body(new ApiError(req.getRequestURI(), "Invalid data violates database constraints: " + detail, 400));
    }

    // ---------- 401 — UNAUTHORIZED (not logged in / bad credentials) ----------

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiError> handleAuth401(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(req.getRequestURI(), "Invalid credentials", 401));
    }

    // ----------403 — FORBIDDEN (logged in but not allowed) ----------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handle403(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(req.getRequestURI(), "Forbidden: " + ex.getMessage(), 403));
    }

    // ---------- 404 — NOT FOUND ----------

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handle404(NotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(req.getRequestURI(), ex.getMessage(), 404));
    }

    // ---------- Respect any explicit ResponseStatusException ----------

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        int code = ex.getStatusCode().value();
        String msg = ex.getReason() != null ? ex.getReason() : ex.getMessage();

        return ResponseEntity.status(code).body(new ApiError(req.getRequestURI(), msg, code));
    }

    // ---------- 500 — UNKNOWN SERVER ERROR ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle500(Exception ex, HttpServletRequest req) {

        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(req.getRequestURI(), "Internal server error", 500));
    }
}
