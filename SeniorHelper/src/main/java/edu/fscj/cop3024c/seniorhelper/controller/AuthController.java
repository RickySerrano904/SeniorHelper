package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.LoginRequest;
import edu.fscj.cop3024c.seniorhelper.model.LoginResponse;
import edu.fscj.cop3024c.seniorhelper.model.RegisterRequest;
import edu.fscj.cop3024c.seniorhelper.model.UserDto;
import edu.fscj.cop3024c.seniorhelper.service.AuthService;
import edu.fscj.cop3024c.seniorhelper.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // ---------- Login (public) ----------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {

        final String username = req.getUsername();
        Profiler profiler = new Profiler("login");
        profiler.start("authService.login");

        try {
            LoginResponse resp = authService.login(req);
            logger.info("Login successful for user: {}", username);
            return ResponseEntity.ok(resp);
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Register (public) ----------
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest req) {
        UserDto registered = userService.register(req);
        logger.info("Registration successful for user: {}", registered.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    // ---------- Logout ----------
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal User me, HttpServletRequest request) {

        Profiler profiler = new Profiler("logout");
        profiler.start("authService.logout");

        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
            }

            String token = authorizationHeader.substring(7).trim();
            if (token.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid token");
            }

            authService.logout(token); // revoke just this token
            SecurityContextHolder.clearContext();

            String who = (me != null) ? me.getUsername() : "user";
            return ResponseEntity.ok(Map.of("message", who + " logged out successfully"));

        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
}
