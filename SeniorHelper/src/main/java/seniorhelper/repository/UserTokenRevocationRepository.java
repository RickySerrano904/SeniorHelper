package seniorhelper.repository;

import seniorhelper.entities.UserTokenRevocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRevocationRepository extends JpaRepository<UserTokenRevocation, String> {
}
