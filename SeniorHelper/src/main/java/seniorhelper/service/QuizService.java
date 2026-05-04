package seniorhelper.service;

import seniorhelper.entities.Quiz;
import seniorhelper.entities.Module;
import seniorhelper.error.NotFoundException;
import seniorhelper.model.QuizDto;
import seniorhelper.repository.ModuleRepository;
import seniorhelper.repository.QuizRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionService questionService;

    public QuizService(QuizRepository quizRepository, ModuleRepository moduleRepository,
                       QuestionService questionService) {
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.questionService = questionService;
    }

    // Create a new quiz for a module.
    @Transactional
    public QuizDto createQuiz(Integer moduleId, QuizDto quizDto) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with ID: " + moduleId));

        // Check if module already has a quiz, if it does - stop before an invalid state occurs.
        if (module.getQuiz() != null) {
            throw new IllegalStateException("Module already has a quiz assigned.");
        }

        Quiz quiz = new Quiz();
        quiz.setName(quizDto.getName());
        quiz.setModule(module);

        Quiz saved = quizRepository.save(quiz);
        module.setQuiz(saved);
        moduleRepository.save(module);
        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public QuizDto findQuiz(Integer moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with ID: " + moduleId));

        Quiz quiz = module.getQuiz();
        if (quiz == null) {
            throw new NotFoundException("Quiz not found for module ID: " + moduleId);
        }
        return convertToDto(quiz);
    }

    @Transactional
    public QuizDto updateQuiz(Integer moduleId, QuizDto quizDto) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with ID: " + moduleId));

        Quiz quiz = module.getQuiz();
        if (quiz == null) {
            throw new NotFoundException("Quiz not found for module ID: " + moduleId);
        }

        quiz.setName(quizDto.getName());
        Quiz updated = quizRepository.save(quiz);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteQuiz(Integer moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with ID: " + moduleId));

        Quiz quiz = module.getQuiz();
        if (quiz != null) {
            module.setQuiz(null);
            moduleRepository.save(module);
            quizRepository.delete(quiz);
        }
    }

    public QuizDto convertToDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setName(quiz.getName());
        dto.setQuestions(
                quiz.getQuestions()
                        .stream()
                        .map(questionService::convertToDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
