package seniorhelper.controller;

import seniorhelper.entities.User;
import seniorhelper.enums.Role;
import seniorhelper.model.UserDto;
import seniorhelper.repository.UserRepository;
import seniorhelper.service.AuthService;
import seniorhelper.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserController(UserService userService,
                          UserRepository userRepository,
                          AuthService authService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    // ---------- GET your own profile ----------
    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal User me) {
        return userService.convertToDTO(me);
    }

    // ---------- GET ALL USERS — ADMIN only ----------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    // ---------- GET ONE SENIOR (admin|self|linked caregiver) ----------
    @PreAuthorize("@permissionChecker.hasPermission(principal, #id)")
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    // ---------- GET ALL SENIORS — ADMIN only ----------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/seniors")
    public List<UserDto> listSeniors() {
        return userRepository.findByRole(Role.SENIOR).stream()
                .map(userService::convertToDTO)
                .toList();
    }

    // ---------- CREATE A NEW USER ----------
    // only ADMIN may create ADMINs; non-admins may create non-admins
    @PostMapping
    public UserDto createUser(@AuthenticationPrincipal User requester,
                              @Valid @RequestBody UserDto body) {

        String roleStr = body.getRole();
        boolean creatingAdmin = roleStr != null && roleStr.trim().equalsIgnoreCase("ADMIN");

        if (creatingAdmin && requester.getRole() != Role.ADMIN) {
            log.warn("USER_CREATE_DENIED requester={} attempted to create ADMIN user {}",
                    requester.getUsername(), body.getUsername());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators may create admin accounts");
        }

        log.info("USER_CREATE_REQUEST by={} username={} role={}",
                requester.getUsername(), body.getUsername(), body.getRole());

        Profiler profiler = new Profiler("createUser");
        profiler.start("userService.save");
        try {
            User saved = userService.save(body);
            log.info("USER_CREATED id={} username={} role={}",
                    saved.getId(), saved.getUsername(), saved.getRole());
            return userService.convertToDTO(saved);
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- EDIT A USER ----------
    // non-admins can edit self only; only admins may change roles
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    @PutMapping("/{id}")
    public UserDto updateUser(@AuthenticationPrincipal User requester,
                              @PathVariable Integer id,
                              @Valid @RequestBody UserDto body) {

        boolean admin = requester.getRole() == Role.ADMIN;

        // Only admins may change any user's role (including their own)
        String roleStr = body.getRole();
        boolean changingRole = roleStr != null && !roleStr.isBlank();
        if (!admin && changingRole) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators may change user roles");
        }

        log.info("USER_UPDATE_REQUEST by={} id={} username={} role={}",
                requester.getUsername(), id, body.getUsername(), body.getRole());

        return userService.updateUser(id, body);
    }

    // ---------- DELETE A USER ----------
    // Admins can delete anyone, users can only self-delete
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        // Revoke tokens, then delete
        authService.logoutAllForUser(id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
