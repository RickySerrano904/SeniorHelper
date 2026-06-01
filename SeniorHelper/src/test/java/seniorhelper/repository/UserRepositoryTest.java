package seniorhelper.repository;

import seniorhelper.entities.User;
import seniorhelper.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    private static final String PASSWORD_HASH =
            "$2a$10$vlloF3RsRY9.bVuzEVXi1eMT1utDA9yz3IUATxcO2URWBtbmp2C0e";

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_and_findById_works() {
        User u = new User();
        u.setUsername("testuser");
        u.setRole(Role.SENIOR);
        u.setPasswordHash(PASSWORD_HASH);

        User saved = userRepository.save(u);

        assertThat(saved.getId()).isNotNull();

        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findAll_returnsSavedUsers() {
        User u1 = new User();
        u1.setUsername("user1");
        u1.setRole(Role.SENIOR);
        u1.setPasswordHash(PASSWORD_HASH);

        User u2 = new User();
        u2.setUsername("user2");
        u2.setRole(Role.CAREGIVER);
        u2.setPasswordHash(PASSWORD_HASH);

        userRepository.save(u1);
        userRepository.save(u2);

        List<User> users = userRepository.findAll();

        assertThat(users)
                .extracting(User::getUsername)
                .contains("user1", "user2");
    }
}
