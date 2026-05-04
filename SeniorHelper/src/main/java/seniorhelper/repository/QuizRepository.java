package seniorhelper.repository;

import seniorhelper.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByModuleId(Integer moduleId);
    void deleteByModuleId(Integer moduleId);
    boolean existsByModuleId(Integer moduleId);
}