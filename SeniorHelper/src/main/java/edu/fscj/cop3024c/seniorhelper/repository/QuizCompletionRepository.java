package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.Quiz;
import edu.fscj.cop3024c.seniorhelper.entities.QuizCompletion;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizCompletionRepository extends JpaRepository<QuizCompletion, Integer> {
    List<QuizCompletion> findAllByUser(User user);
    void deleteByUserAndQuiz(User user, Quiz quiz);
}
