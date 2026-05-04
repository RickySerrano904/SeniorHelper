package seniorhelper.repository;

import seniorhelper.entities.Quiz;
import seniorhelper.entities.QuizCompletion;
import seniorhelper.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizCompletionRepository extends JpaRepository<QuizCompletion, Integer> {
    List<QuizCompletion> findAllByUser(User user);
    void deleteByUserAndQuiz(User user, Quiz quiz);
}
