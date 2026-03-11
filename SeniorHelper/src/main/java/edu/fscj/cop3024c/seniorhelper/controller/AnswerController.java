package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.AnswerDto;
import edu.fscj.cop3024c.seniorhelper.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import java.util.*;

@RestController
@RequestMapping("/api/modules/{moduleId}/quiz/questions/{questionId}/answers")
public class AnswerController {

    @Autowired private final AnswerService answerService;
    public AnswerController(AnswerService answerService) { this.answerService = answerService; }

    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

    // 1. Create a new answer.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnswerDto> create(@PathVariable Integer moduleId, @PathVariable Integer questionId, @Valid @RequestBody AnswerDto dto) {
        Profiler profiler = new Profiler("createAnswer");
        profiler.start("Create Answer");
        logger.info("Attempting to create a new answer for question {} in module {}'s quiz", questionId, moduleId);
        try {
            AnswerDto created = answerService.createAnswer(moduleId, questionId, dto);
            logger.info("Successfully created a new answer with ID {} in question {} for module {}'s quiz", created.getId(), questionId, moduleId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            logger.error("Error creating a new answer in question {} for module {}'s quiz, {}", questionId, moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 2. Retrieve all answers.
    @GetMapping
    public ResponseEntity<List<AnswerDto>> getAnswers(@PathVariable Integer moduleId, @PathVariable Integer questionId) {
        List<AnswerDto> answers = answerService.findAnswersByQuestion(moduleId, questionId);
        return new ResponseEntity<>(answers, HttpStatus.OK);
    }
    // 3. Update an existing answer.
    @PutMapping("/{answerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable Integer moduleId, @PathVariable Integer questionId,
                                                  @PathVariable Integer answerId, @Valid @RequestBody AnswerDto dto) {
        Profiler profiler = new Profiler("updateAnswer");
        profiler.start("Update Answer");
        logger.info("Attempting to update answer {} in question {} for module {}'s quiz", answerId, questionId, moduleId);
        try {
            AnswerDto updated = answerService.updateAnswer(moduleId, questionId, answerId, dto);
            logger.info("Successfully updated answer {} in question {} for module {}'s quiz", updated.getId(), questionId, moduleId);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            logger.error("Error updating answer in question {} for module {}'s quiz, {}", questionId, moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 4. Delete an answer.
    @DeleteMapping("/{answerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Integer moduleId, @PathVariable Integer questionId, @PathVariable Integer answerId) {
        Profiler profiler = new Profiler("deleteAnswer");
        profiler.start("Delete Answer");
        logger.info("Attempting to delete answer {} in question {} for module {}'s quiz", answerId, questionId, moduleId);
        try {
            answerService.deleteAnswer(moduleId, questionId, answerId);
            logger.info("Successfully deleted answer in question {} from module {}'s quiz", questionId, moduleId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting answer {} in question {} for module {}'s quiz, {}", answerId, questionId, moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
}