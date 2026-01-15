package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.LoginRequest;
import edu.fscj.cop3024c.seniorhelper.model.LoginResponse;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;

    // In-memory token store (token -> username) and expiration
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpiry = new ConcurrentHashMap<>();

    // 12 hours TTL
    private static final long TOKEN_TTL_MS = 12L * 60 * 60 * 1000;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Login
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();
        if (username == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password required");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (user.getSalt() == null || user.getHash() == null) {
            // User exists but hasn't been migrated to salted+hashed password
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not password-enabled");
        }
        // Hash incoming password with stored salt and compare
        String computedHash = UserService.hashPassword(password, user.getSalt());
        if (!computedHash.equals(user.getHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        // Issue token
        String token = issueTokenForUser(user.getUsername());
        return new LoginResponse(token, user.getUsername() + " logged on successfully");
    }

    // Logout
    public void logout(String token) {
        if (token == null || token.isBlank()) return;
        tokenToUser.remove(token);
        tokenExpiry.remove(token);
    }

    // Logout ALL sessions for a user (used for self-delete or admin delete)
    public void logoutAllForUser(Integer userId) {
        if (userId == null) return;

        // get username from repository
        String username = userRepository.findById(userId)
                .map(User::getUsername)
                .orElse(null);
        if (username == null) return;

        // remove all tokens belonging to this username
        tokenToUser.entrySet().removeIf(e -> username.equals(e.getValue()));

        // remove expirations for those tokens as well
        tokenExpiry.entrySet().removeIf(e -> {
            String mappedUser = tokenToUser.get(e.getKey());
            return username.equals(mappedUser);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();

        // Expiration check
        Long exp = tokenExpiry.get(token);
        if (exp == null || exp < Instant.now().toEpochMilli()) {
            logout(token);
            return Optional.empty();
        }

        String username = tokenToUser.get(token);
        if (username == null) return Optional.empty();

        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User requireUserByToken(String token) {
        return findUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
    }

    private String issueTokenForUser(String username) {
        String token = UUID.randomUUID().toString();
        tokenToUser.put(token, username);
        tokenExpiry.put(token, Instant.now().toEpochMilli() + TOKEN_TTL_MS);
        return token;
    }
}