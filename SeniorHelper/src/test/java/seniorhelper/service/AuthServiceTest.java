package seniorhelper.service;
import seniorhelper.entities.User;
import seniorhelper.model.LoginRequest;
import seniorhelper.model.LoginResponse;
import seniorhelper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SEEDED_PASSWORD_HASH =
            "$2a$10$vlloF3RsRY9.bVuzEVXi1eMT1utDA9yz3IUATxcO2URWBtbmp2C0e";

    @Mock
    private UserRepository userRepository;

    private AuthService authService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder);
    }

    // ---------------------------------------------------------
    // login() - happy path
    // ---------------------------------------------------------
    @Test
    void login_ShouldReturnTokenAndMessage_WhenCredentialsAreValid() {
        // Given
        String username = "john";
        String rawPassword = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest(username, rawPassword);

        // When
        LoginResponse resp = authService.login(req);

        // Then
        assertThat(resp).isNotNull();
        assertThat(resp.getToken()).isNotBlank();
        assertThat(resp.getToken().split("\\.")).hasSize(3);
        assertThat(resp.getMessage()).contains(username + " logged on successfully");

        // Token should resolve back to the same user
        Optional<User> byToken = authService.findUserByToken(resp.getToken());
        assertThat(byToken).isPresent();
        assertThat(byToken.get().getUsername()).isEqualTo(username);

        // login() + findUserByToken() each call findByUsername
        verify(userRepository, atLeastOnce()).findByUsername(username);
    }

    // ---------------------------------------------------------
    // login() - missing username or password
    // ---------------------------------------------------------
    @Test
    void login_ShouldThrowBadRequest_WhenUsernameOrPasswordMissing() {
        // Missing username
        LoginRequest req1 = new LoginRequest(null, "pw");
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> authService.login(req1));
        assertThat(ex1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex1.getReason()).contains("Username and password required");

        // Missing password
        LoginRequest req2 = new LoginRequest("john", null);
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> authService.login(req2));
        assertThat(ex2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex2.getReason()).contains("Username and password required");

        verify(userRepository, never()).findByUsername(anyString());
    }

    // ---------------------------------------------------------
    // login() - unknown user
    // ---------------------------------------------------------
    @Test
    void login_ShouldThrowUnauthorized_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest("unknown", "pw");

        // When
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));

        // Then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Invalid credentials");
    }

    // ---------------------------------------------------------
    // login() - wrong password
    // ---------------------------------------------------------
    @Test
    void login_ShouldThrowUnauthorized_WhenPasswordIsWrong() {
        // Given
        String username = "john";

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("correctPassword"));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest(username, "wrongPassword");

        // When
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));

        // Then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Invalid credentials");
    }

    @Test
    void login_ShouldAcceptSeededBCryptPasswordHash() {
        String username = "JohnSenior";

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(SEEDED_PASSWORD_HASH);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginResponse resp = authService.login(new LoginRequest(username, "password"));

        assertThat(resp.getToken()).isNotBlank();
        assertThat(resp.getMessage()).contains(username + " logged on successfully");
    }

    // ---------------------------------------------------------
    // logout() - removes token
    // ---------------------------------------------------------
    @Test
    void logout_ShouldInvalidateToken() {
        String username = "john";
        String rawPassword = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginResponse resp = authService.login(new LoginRequest(username, rawPassword));
        String token = resp.getToken();

        // Sanity check token works
        assertThat(authService.findUserByToken(token)).isPresent();

        // When
        authService.logout(token);

        // Then
        assertThat(authService.findUserByToken(token)).isEmpty();
    }

    // ---------------------------------------------------------
    // requireUserByToken() - invalid token
    // ---------------------------------------------------------
    @Test
    void requireUserByToken_ShouldThrowUnauthorized_WhenTokenInvalid() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.requireUserByToken("some-invalid-token"));
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Invalid or missing token");
    }
}
