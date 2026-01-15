package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Module;
import edu.fscj.cop3024c.seniorhelper.entities.Question;
import edu.fscj.cop3024c.seniorhelper.entities.Quiz;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.QuestionDto;
import edu.fscj.cop3024c.seniorhelper.repository.ModuleRepository;
import edu.fscj.cop3024c.seniorhelper.repository.QuizRepository;
import edu.fscj.cop3024c.seniorhelper.repository.QuestionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final ModuleRepository moduleRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    public QuestionService(ModuleRepository moduleRepository, QuizRepository quizRepository,
                           QuestionRepository questionRepository, AnswerService answerService) {
        this.moduleRepository = moduleRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    @Transactional
    public QuestionDto createQuestion(Integer moduleId, QuestionDto dto) {
        Quiz quiz = verifyModuleQuiz(moduleId);

        Question question = new Question();
        question.setText(dto.getText());
        question.setQuiz(quiz);
        Question saved = questionRepository.save(question);
        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> findAllQuestions(Integer moduleId) {
        Quiz quiz = verifyModuleQuiz(moduleId);
        return questionRepository.findByQuizId(quiz.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDto updateQuestion(Integer moduleId, Integer questionId, QuestionDto dto) {
        Quiz quiz = verifyModuleQuiz(moduleId);
        Question question = questionRepository.findByIdAndQuizId(questionId, quiz.getId())
                .orElseThrow(() -> new NotFoundException("Question not found"));

        question.setText(dto.getText());
        Question updated = questionRepository.save(question);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteQuestion(Integer moduleId, Integer questionId) {
        Quiz quiz = verifyModuleQuiz(moduleId);
        Question question = questionRepository.findByIdAndQuizId(questionId, quiz.getId())
                .orElseThrow(() -> new NotFoundException("Question not found"));

        questionRepository.delete(question);
    }

    private Quiz verifyModuleQuiz(Integer moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found"));

        return quizRepository.findByModuleId(module.getId())
                .orElseThrow(() -> new NotFoundException("Quiz not found for module"));
    }

    public QuestionDto convertToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setText(question.getText());
        dto.setAnswers(
                question.getAnswers()
                        .stream()
                        .map(answerService::convertToDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}