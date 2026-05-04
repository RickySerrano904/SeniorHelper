package seniorhelper.repository;

import seniorhelper.entities.Lesson;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> findByModuleId(Integer moduleId);

    // Load a lesson only if it belongs to moduleId
    java.util.Optional<Lesson> findByIdAndModuleId(Integer lessonId, Integer moduleId);
}