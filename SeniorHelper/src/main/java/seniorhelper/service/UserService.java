package seniorhelper.service;

import seniorhelper.model.UserDto;
import seniorhelper.model.RegisterRequest;
import seniorhelper.entities.User;
import seniorhelper.error.NotFoundException;
import seniorhelper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;
import seniorhelper.enums.Role;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== CRUD Methods with DTO conversion =====

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return convertToDTO(user);
    }

    // Helper to return raw entity (for updates/deletes)
    @Transactional(readOnly = true)
    public User findByIdEntity(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Transactional
    public UserDto register(RegisterRequest req) {
        String role = trimOrNull(req.getRole());
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }
        if (role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be created from registration");
        }

        String username = trimOrNull(req.getUsername());
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        String email = trimOrNull(req.getEmail());
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        email = email.toLowerCase(Locale.ROOT);
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName(req.getFirstName());
        dto.setLastName(req.getLastName());
        dto.setPassword(req.getPassword());
        dto.setRole(role);

        User saved = save(dto);
        return convertToDTO(saved);
    }

    @Transactional
    public User save(UserDto dto) {
        User user = new User();
        user.setUsername(trimOrNull(dto.getUsername()));
        user.setEmail(trimOrNull(dto.getEmail()));
        user.setFirstName(trimOrNull(dto.getFirstName()));
        user.setLastName(trimOrNull(dto.getLastName()));

        // set role from DTO (default to SENIOR if missing)
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(dto.getRole().trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid role: " + dto.getRole() + " (allowed: SENIOR, CAREGIVER, ADMIN)"
                );
            }
        } else {
            user.setRole(Role.SENIOR);
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public UserDto updateUser(Integer id, UserDto userDetails) {
        User existingUser = findByIdEntity(id);
        existingUser.setUsername(trimOrNull(userDetails.getUsername()));
        existingUser.setEmail(trimOrNull(userDetails.getEmail()));
        existingUser.setFirstName(trimOrNull(userDetails.getFirstName()));
        existingUser.setLastName(trimOrNull(userDetails.getLastName()));

        if (userDetails.getRole() != null && !userDetails.getRole().isBlank()) {
            try {
                existingUser.setRole(Role.valueOf(userDetails.getRole().trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid role: " + userDetails.getRole() + " (allowed: SENIOR, CAREGIVER, ADMIN)"
                );
            }
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPassword()));
        }
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public UserDto convertToDTO(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    private String trimOrNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
