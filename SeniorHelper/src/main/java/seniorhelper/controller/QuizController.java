package seniorhelper.controller;

import seniorhelper.model.QuizDto;
import seniorhelper.service.QuizService;
import jakarta.validation.Valid;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

@RestController
@RequestMapping("/api/modules/{moduleId}/quiz")
public class QuizController {

    @Autowired private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    /* ----- Quiz Endpoints ----- */
    // 1. Create a new quiz.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> create(@PathVariable Integer moduleId, @Valid @RequestBody QuizDto quizDto) {
        Profiler profiler = new Profiler("createQuiz");
        profiler.start("Create Quiz");

        logger.info("Attempting to create a new quiz in module ID {}", moduleId);
        try {
            QuizDto created = quizService.createQuiz(moduleId, quizDto);
            logger.info("Successfully created a new quiz with ID {} in module {}", created.getId(), moduleId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating a new quiz in module ID {}, {}", moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 2. Retrieve an existing quiz.
    @GetMapping
    public ResponseEntity<QuizDto> getQuiz(@PathVariable Integer moduleId) {
        QuizDto quiz = quizService.findQuiz(moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(quiz);
    }
    // 3. Update an existing quiz.
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> update(@PathVariable Integer moduleId, @RequestBody QuizDto quizDto) {
        Profiler profiler = new Profiler("updateQuiz");
        profiler.start("Update Quiz");

        logger.info("Attempting to update quiz in module ID {}", moduleId);
        try {
            QuizDto updated = quizService.updateQuiz(moduleId, quizDto);
            logger.info("Successfully updated quiz with ID {} in module {}", updated.getId(), moduleId);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            logger.error("Error updating quiz in module ID {}, {}", moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 4. Delete a quiz.
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> delete(@PathVariable Integer moduleId) {
        Profiler profiler = new Profiler("deleteQuiz");
        profiler.start("Delete Quiz");

        logger.debug("Attempting to delete quiz from module ID {}", moduleId);
        try {
            quizService.deleteQuiz(moduleId);
            logger.info("Successfully deleted quiz from module ID {}", moduleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting quiz in module ID {}, {}", moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
}