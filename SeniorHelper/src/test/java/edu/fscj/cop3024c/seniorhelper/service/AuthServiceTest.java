package edu.fscj.cop3024c.seniorhelper.service;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.LoginRequest;
import edu.fscj.cop3024c.seniorhelper.model.LoginResponse;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    // ---------------------------------------------------------
    // login() - happy path
    // ---------------------------------------------------------
    @Test
    void login_ShouldReturnTokenAndMessage_WhenCredentialsAreValid() {
        // Given
        String username = "john";
        String rawPassword = "password123";

        // Generate a valid salt and hash using the real UserService logic
        String salt = UserService.generateSalt();
        String hash = UserService.hashPassword(rawPassword, salt);

        User user = new User();
        user.setUsername(username);
        user.setSalt(salt);
        user.setHash(hash);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest(username, rawPassword);

        // When
        LoginResponse resp = authService.login(req);

        // Then
        assertThat(resp).isNotNull();
        assertThat(resp.getToken()).isNotBlank();
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

        // Create a proper salt + hash for a *different* password
        String salt = UserService.generateSalt();
        String correctHash = UserService.hashPassword("correctPassword", salt);

        User user = new User();
        user.setUsername(username);
        user.setSalt(salt);
        user.setHash(correctHash);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        LoginRequest req = new LoginRequest(username, "wrongPassword");

        // When
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(req));

        // Then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ex.getReason()).contains("Invalid credentials");
    }

    // ---------------------------------------------------------
    // logout() - removes token
    // ---------------------------------------------------------
    @Test
    void logout_ShouldInvalidateToken() {
        String username = "john";
        String rawPassword = "password123";

        String salt = UserService.generateSalt();
        String hash = UserService.hashPassword(rawPassword, salt);

        User user = new User();
        user.setUsername(username);
        user.setSalt(salt);
        user.setHash(hash);

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
