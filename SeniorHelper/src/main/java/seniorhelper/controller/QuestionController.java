package seniorhelper.controller;

import seniorhelper.model.QuestionDto;
import seniorhelper.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@RestController
@RequestMapping("/api/modules/{moduleId}/quiz/questions")
public class QuestionController {

    @Autowired private final QuestionService questionService;
    public QuestionController(QuestionService questionService) { this.questionService = questionService; }

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    /* ----- Question Endpoints ----- */
    // 1. Create a new question.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionDto> create(@PathVariable Integer moduleId, @Valid @RequestBody QuestionDto questionDto) {
        logger.info("Attempting to create a new question in quiz for module ID {}", moduleId);
        try {
            QuestionDto created = questionService.createQuestion(moduleId, questionDto);
            logger.info("Successfully created a new question with ID {} in module {}'s quiz", created.getId(), moduleId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating a new question in quiz for module ID {}, {}", moduleId, e.getMessage());
            throw e;
        }
    }
    // 2. Retrieve all questions for a quiz.
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAll(@PathVariable Integer moduleId) {
        List<QuestionDto> questions = questionService.findAllQuestions(moduleId);
        return ResponseEntity.ok(questions);
    }
    // 3. Update a question.
    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionDto> update(@PathVariable Integer moduleId, @PathVariable Integer questionId,
                                              @Valid @RequestBody QuestionDto questionDto) {
        logger.info("Attempting to update question {} in module {}'s quiz", questionId, moduleId);
        try {
            QuestionDto updated = questionService.updateQuestion(moduleId, questionId, questionDto);
            logger.info("Successfully updated question {} in module {}'s quiz", questionId, moduleId);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            logger.error("Error updating question {} in quiz for module ID {}, {}", questionId, moduleId, e.getMessage());
            throw e;
        }
    }
    // 4. Delete a question.
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer moduleId, @PathVariable Integer questionId) {
        logger.info("Attempting to delete question {} in module {}'s quiz", questionId, moduleId);
        try {
            questionService.deleteQuestion(moduleId, questionId);
            logger.info("Successfully deleted question from module {}'s quiz", moduleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting question {} in module {}'s quiz, {}", questionId, moduleId, e.getMessage());
            throw e;
        }
    }
}
