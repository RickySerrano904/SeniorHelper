package edu.fscj.cop3024c.seniorhelper.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.LoginRequest;
import edu.fscj.cop3024c.seniorhelper.model.LoginResponse;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;

    // token JTI -> exp epoch-seconds (revoked until token naturally expires)
    private final Map<String, Long> revokedJtiExp = new ConcurrentHashMap<>();
    // username -> revoke cutoff (tokens issued at/before this instant are invalid)
    private final Map<String, Long> userRevokedAfter = new ConcurrentHashMap<>();

    // Defaults are used in tests unless overridden by Spring properties.
    @Value("${app.security.jwt.secret:dev-jwt-secret-change-me-please-dev-jwt-secret}")
    private String jwtSecret = "dev-jwt-secret-change-me-please-dev-jwt-secret";

    @Value("${app.security.jwt.ttl-ms:3600000}")
    private long tokenTtlMs = 60L * 60 * 1000; // 60m default

    private volatile JwtEncoder jwtEncoder;
    private volatile JwtDecoder jwtDecoder;

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
        // Issue JWT token
        String token = issueTokenForUser(user);
        return new LoginResponse(token, user.getUsername() + " logged on successfully");
    }

    // Logout
    public void logout(String token) {
        if (token == null || token.isBlank()) return;
        parseAndValidateToken(token).ifPresent(claims -> {
            revokedJtiExp.put(claims.jti(), claims.exp());
        });
        pruneRevocations();
    }

    // Logout ALL sessions for a user (used for self-delete or admin delete)
    public void logoutAllForUser(Integer userId) {
        if (userId == null) return;

        // get username from repository
        String username = userRepository.findById(userId)
                .map(User::getUsername)
                .orElse(null);
        if (username == null) return;

        userRevokedAfter.put(username, Instant.now().getEpochSecond());
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        pruneRevocations();

        Optional<JwtClaims> claimsOpt = parseAndValidateToken(token);
        if (claimsOpt.isEmpty()) {
            return Optional.empty();
        }

        JwtClaims claims = claimsOpt.get();
        long now = Instant.now().getEpochSecond();

        Long revokedExp = revokedJtiExp.get(claims.jti());
        if (revokedExp != null && revokedExp > now) {
            return Optional.empty();
        }

        Long userCutoff = userRevokedAfter.get(claims.username());
        if (userCutoff != null && claims.iat() <= userCutoff) {
            return Optional.empty();
        }

        return userRepository.findByUsername(claims.username());
    }

    @Transactional(readOnly = true)
    public User requireUserByToken(String token) {
        return findUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token"));
    }

    private String issueTokenForUser(User user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(Math.max(1L, tokenTtlMs));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return jwtEncoder().encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private Optional<JwtClaims> parseAndValidateToken(String token) {
        try {
            Jwt jwt = jwtDecoder().decode(token);
            String username = jwt.getSubject();
            String jti = jwt.getId();
            Instant issuedAt = jwt.getIssuedAt();
            Instant expiresAt = jwt.getExpiresAt();

            if (username == null || username.isBlank()
                    || jti == null || jti.isBlank()
                    || issuedAt == null || expiresAt == null) {
                return Optional.empty();
            }

            return Optional.of(new JwtClaims(
                    username,
                    jti,
                    issuedAt.getEpochSecond(),
                    expiresAt.getEpochSecond()));
        } catch (JwtException ex) {
            return Optional.empty();
        }
    }

    private void pruneRevocations() {
        long now = Instant.now().getEpochSecond();
        revokedJtiExp.entrySet().removeIf(e -> e.getValue() == null || e.getValue() <= now);
    }

    private JwtEncoder jwtEncoder() {
        JwtEncoder local = jwtEncoder;
        if (local == null) {
            local = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey()));
            jwtEncoder = local;
        }
        return local;
    }

    private JwtDecoder jwtDecoder() {
        JwtDecoder local = jwtDecoder;
        if (local == null) {
            local = NimbusJwtDecoder.withSecretKey(secretKey())
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
            jwtDecoder = local;
        }
        return local;
    }

    private SecretKey secretKey() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT secret must not be blank");
        }
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private record JwtClaims(String username, String jti, long iat, long exp) {}
}
