package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.LoginRequest;
import edu.fscj.cop3024c.seniorhelper.model.LoginResponse;
import edu.fscj.cop3024c.seniorhelper.model.RegisterRequest;
import edu.fscj.cop3024c.seniorhelper.model.UserDto;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
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
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, UserService userService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
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

        } catch (org.springframework.security.core.AuthenticationException ex) {
            // Auth failures throw 401
            logger.warn("Login failed for {}", username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", ex);

        } catch (ResponseStatusException ex) {
            throw ex;

        } catch (Exception ex) {
            // Non-auth errors throw 500
            logger.error("Login error for {}: {}", username, ex.getMessage(), ex);
            throw ex;

        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Register (public) ----------
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest req) {
        String role = req.getRole();
        if (role != null && role.trim().equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be created from registration");
        }

        String username = req.getUsername().trim();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(req.getPassword());
        dto.setRole(role);

        var saved = userService.save(dto);
        logger.info("Registration successful for user: {}", username);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToDTO(saved));
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
