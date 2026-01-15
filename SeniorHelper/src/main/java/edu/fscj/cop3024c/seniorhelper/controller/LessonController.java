package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.model.LessonDto;
import edu.fscj.cop3024c.seniorhelper.service.LessonService;
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
import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons")
public class LessonController {

    @Autowired private final LessonService lessonService;
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    private static final Logger logger = LoggerFactory.getLogger(LessonController.class);

    /* ----- Lesson Endpoints ----- */
    // 1. Retrieve existing lessons by module.
    @GetMapping
    public List<LessonDto> getLessonsByModuleId(@PathVariable Integer moduleId) {
        return lessonService.findLessonsByModuleId(moduleId);
    }
    // 2. Retrieve lesson within a module.
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDto> getLessonInModule(@PathVariable Integer moduleId,
                                                       @PathVariable Integer lessonId) {
        LessonDto foundLesson = lessonService.findLessonById(moduleId, lessonId);
        return ResponseEntity.ok(foundLesson);
    }
    // 3. Create a new lesson.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonDto> createLesson(@PathVariable Integer moduleId,
                                                  @Valid @RequestBody LessonDto lessonDto) {
        Profiler profiler = new Profiler("createLesson");
        profiler.start("Create Lesson");

        logger.info("Attempting to create a new lesson in module ID {}", moduleId);
        try {
            LessonDto created = lessonService.createLesson(moduleId, lessonDto);
            logger.info("Successfully created a new lesson with ID {} in module {}", created.getId(), moduleId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating a new lesson in module ID {}, {}", moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 4. Update existing lesson.
    @PutMapping("/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonDto> updateLesson(@PathVariable Integer moduleId, @PathVariable Integer lessonId,
                                                  @RequestBody LessonDto lessonDto) {
        Profiler profiler = new Profiler("updateLesson");
        profiler.start("Update Lesson");

        logger.info("Attempting to update lesson ID {} in module ID {}", lessonId, moduleId);
        try {
            LessonDto updated = lessonService.updateLesson(moduleId, lessonId, lessonDto);
            logger.info("Successfully updated lesson ID {} in module ID {}", lessonId, moduleId);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            logger.error("Error updating lesson in module ID {}, {}", moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
    // 5. Delete lesson by its ID.
    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer moduleId, @PathVariable Integer lessonId) {
        Profiler profiler = new Profiler("deleteLesson");
        profiler.start("Delete Lesson");

        logger.debug("Attempting to delete lesson {} from module ID {}", lessonId, moduleId);
        try {
            lessonService.deleteLesson(moduleId, lessonId);
            logger.info("Successfully deleted lesson {} from module ID {}", lessonId, moduleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting lesson {} from module ID {}, {}", lessonId, moduleId, e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }
}