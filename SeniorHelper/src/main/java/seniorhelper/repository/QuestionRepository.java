package seniorhelper.repository;

import seniorhelper.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuizId(Integer quizId);
    Optional<Question> findByIdAndQuizId(Integer questionId, Integer quizId);
}