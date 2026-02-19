package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.RegisterRequest;
import edu.fscj.cop3024c.seniorhelper.model.UserDto;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ---------- findAll ----------

    @Test
    void findAll_returnsDtos() {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("senior1");
        u1.setRole(Role.SENIOR);

        User u2 = new User();
        u2.setId(2);
        u2.setUsername("caregiver1");
        u2.setRole(Role.CAREGIVER);

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UserDto> result = userService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserDto::getUsername)
                .containsExactlyInAnyOrder("senior1", "caregiver1");
    }

    // ---------- findById ----------

    @Test
    void findById_returnsDto_whenFound() {
        User u = new User();
        u.setId(10);
        u.setUsername("family1");
        u.setRole(Role.FAMILY);

        when(userRepository.findById(10)).thenReturn(Optional.of(u));

        UserDto dto = userService.findById(10);

        assertThat(dto.getId()).isEqualTo(10);
        assertThat(dto.getUsername()).isEqualTo("family1");
        assertThat(dto.getRole()).isEqualTo("FAMILY");
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- save(UserDto) ----------

    @Test
    void register_normalizesInput_andReturnsDto() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("  caregiver1  ");
        req.setEmail("  Test@Email.com  ");
        req.setFirstName("  Care  ");
        req.setLastName("  Giver  ");
        req.setPassword("password123");
        req.setRole("Caregiver");

        when(userRepository.findByUsername("caregiver1")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDto saved = userService.register(req);

        assertThat(saved.getUsername()).isEqualTo("caregiver1");
        assertThat(saved.getEmail()).isEqualTo("test@email.com");
        assertThat(saved.getFirstName()).isEqualTo("Care");
        assertThat(saved.getLastName()).isEqualTo("Giver");
        assertThat(saved.getRole()).isEqualTo("CAREGIVER");
    }

    @Test
    void register_throwsConflict_whenUsernameExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existingUser");
        req.setEmail("new@email.com");
        req.setFirstName("First");
        req.setLastName("Last");
        req.setPassword("password123");
        req.setRole("SENIOR");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void register_throwsForbidden_whenAdminRoleRequested() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("adminAttempt");
        req.setEmail("admin@site.com");
        req.setFirstName("Admin");
        req.setLastName("Attempt");
        req.setPassword("password123");
        req.setRole("ADMIN");

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void save_usesRoleFromDto() {
        UserDto dto = new UserDto();
        dto.setUsername("caregiver1");
        dto.setFirstName("Care");
        dto.setLastName("Giver");
        dto.setPassword("pass");
        dto.setRole("CAREGIVER");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.save(dto);

        assertThat(saved.getUsername()).isEqualTo("caregiver1");
        assertThat(saved.getRole()).isEqualTo(Role.CAREGIVER);
    }

    @Test
    void save_defaultsRoleToSenior_whenRoleMissing() {
        UserDto dto = new UserDto();
        dto.setUsername("noRoleUser");
        dto.setFirstName("No");
        dto.setLastName("Role");
        dto.setPassword("pass");
        dto.setRole(null);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.save(dto);

        assertThat(saved.getRole()).isEqualTo(Role.SENIOR);
    }

    @Test
    void save_throwsIllegalArgument_whenRoleInvalid() {
        UserDto dto = new UserDto();
        dto.setUsername("badRoleUser");
        dto.setFirstName("Bad");
        dto.setLastName("Role");
        dto.setPassword("pass");
        dto.setRole("NOT_A_ROLE");

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_deletes_whenExists() {
        when(userRepository.existsById(5)).thenReturn(true);

        userService.deleteById(5);

        verify(userRepository).deleteById(5);
    }

    @Test
    void deleteById_throwsNotFound_whenMissing() {
        when(userRepository.existsById(5)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(5))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }
}
