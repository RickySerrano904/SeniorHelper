package seniorhelper.repository;

import seniorhelper.entities.User;
import seniorhelper.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    // find all users by role
    List<User> findByRole(Role role);

    long countByRole(Role role);
}
