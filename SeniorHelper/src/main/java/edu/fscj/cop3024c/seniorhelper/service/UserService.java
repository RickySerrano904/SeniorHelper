package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.model.UserDto;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User save(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail().trim());
        }

        // set role from DTO (default to SENIOR if missing)
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(dto.getRole().trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid role: " + dto.getRole() + " (allowed: SENIOR, CAREGIVER, FAMILY, ADMIN)"
                );
            }
        } else {
            user.setRole(Role.SENIOR);
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String salt = generateSalt();
            String hash = hashPassword(dto.getPassword(), salt);
            user.setSalt(salt);
            user.setHash(hash);
        }
        return userRepository.save(user);
    }

    @Transactional
    public UserDto updateUser(Integer id, UserDto userDetails) {
        User existingUser = findByIdEntity(id);
        existingUser.setUsername(userDetails.getUsername());
        if (userDetails.getEmail() != null && !userDetails.getEmail().isBlank()) {
            existingUser.setEmail(userDetails.getEmail().trim());
        }

        if (userDetails.getRole() != null && !userDetails.getRole().isBlank()) {
            try {
                existingUser.setRole(Role.valueOf(userDetails.getRole().trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid role: " + userDetails.getRole() + " (allowed: SENIOR, CAREGIVER, FAMILY, ADMIN)"
                );
            }
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            String salt = generateSalt();
            String hash = hashPassword(userDetails.getPassword(), salt);
            existingUser.setSalt(salt);
            existingUser.setHash(hash);
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
                user.getRole() != null ? user.getRole().name() : null
        );
    }

    // ===== Password hashing (PBKDF2WithHmacSHA1) =====
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LEN_BYTES = 32;

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LEN_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String base64Salt) {
        try {
            char[] chars = password.toCharArray();
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
