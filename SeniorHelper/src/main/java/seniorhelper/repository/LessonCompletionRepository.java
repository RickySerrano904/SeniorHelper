package seniorhelper.repository;

import seniorhelper.entities.Lesson;
import seniorhelper.entities.LessonCompletion;
import seniorhelper.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Integer> {
    List<LessonCompletion> findAllByUser(User user);
    boolean existsByUserAndLesson(User user, Lesson lesson);
    Optional<LessonCompletion> findByUserAndLesson(User user, Lesson lesson);
}
