package seniorhelper.repository;

import seniorhelper.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findAllByQuestionId(Integer questionId);
    Optional<Answer> findByIdAndQuestionId(Integer quizId, Integer questionId);
}