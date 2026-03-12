package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.Lesson;
import edu.fscj.cop3024c.seniorhelper.entities.LessonCompletion;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Integer> {
    List<LessonCompletion> findAllByUser(User user);
    boolean existsByUserAndLesson(User user, Lesson lesson);
    Optional<LessonCompletion> findByUserAndLesson(User user, Lesson lesson);
}
