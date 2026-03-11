package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Answer;
import edu.fscj.cop3024c.seniorhelper.entities.Quiz;
import edu.fscj.cop3024c.seniorhelper.entities.Question;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.AnswerDto;
import edu.fscj.cop3024c.seniorhelper.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.*;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public AnswerService(AnswerRepository answerRepository, QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public AnswerDto createAnswer(Integer moduleId, Integer questionId, AnswerDto dto) {
        Question question = verifyQuestionBelongsToModuleQuiz(moduleId, questionId);

        Answer answer = new Answer();
        answer.setText(dto.getText());
        answer.setCorrect(dto.isCorrect());
        answer.setQuestion(question);
        Answer saved = answerRepository.save(answer);
        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AnswerDto> findAnswersByQuestion(Integer moduleId, Integer questionId) {
        Question question = verifyQuestionBelongsToModuleQuiz(moduleId, questionId);

        return question.getAnswers()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnswerDto updateAnswer(Integer moduleId, Integer questionId, Integer answerId, AnswerDto dto) {
        Answer answer = verifyAnswer(moduleId, questionId, answerId);

        answer.setText(dto.getText());
        answer.setCorrect(dto.isCorrect());
        Answer updated = answerRepository.save(answer);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteAnswer(Integer moduleId, Integer questionId, Integer answerId) {
        Answer answer = verifyAnswer(moduleId, questionId, answerId);
        Question question = answer.getQuestion();
        question.getAnswers().remove(answer);
        answerRepository.delete(answer);
    }

    private Question verifyQuestionBelongsToModuleQuiz(Integer moduleId, Integer questionId) {
        Quiz quiz = quizRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new NotFoundException("Quiz not found in module with ID: " + moduleId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found with ID: " + questionId));

        if (!question.getQuiz().getId().equals(quiz.getId())) {
            throw new NotFoundException("Question does not belong to this module's quiz.");
        }
        return question;
    }

    private Answer verifyAnswer(Integer moduleId, Integer questionId, Integer answerId) {
        Question question = verifyQuestionBelongsToModuleQuiz(moduleId, questionId);

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new NotFoundException("Answer not found with ID: " + answerId));

        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new NotFoundException("Answer does not belong to this question.");
        }
        return answer;
    }

    public AnswerDto convertToDto(Answer answer) {
        AnswerDto dto = new AnswerDto();
        dto.setId(answer.getId());
        dto.setText(answer.getText());
        dto.setCorrect(answer.isCorrect());
        return dto;
    }
}