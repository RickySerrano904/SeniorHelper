package seniorhelper.controller;

import seniorhelper.entities.User;
import seniorhelper.model.ProgressDto;
import seniorhelper.service.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final ProgressService progressService;
    private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // ---------- Current user's overall progress ----------
    @GetMapping("/progress")
    public ResponseEntity<ProgressDto> overallProgress(@AuthenticationPrincipal User me) {
        return ResponseEntity.ok(progressService.overall(me));
    }

    // ---------- Progress for a specific senior ----------
    // Admin: any user; Caregiver: must have CareLink; Senior: self
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    @GetMapping("/progress/seniors/{seniorId}")
    public ResponseEntity<ProgressDto> progressForSenior(@PathVariable Integer seniorId,
                                                         @AuthenticationPrincipal User requester) {
        Profiler profiler = new Profiler("progressForSenior");
        profiler.start("progressService.overallForSenior");
        try {
            logger.info("Progress requested by user={} for seniorId={}",
                    requester.getUsername(), seniorId);

            ProgressDto dto = progressService.overallForSenior(seniorId, requester);
            return ResponseEntity.ok(dto);
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Complete a lesson ----------
    @PostMapping("/modules/{moduleId}/lessons/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(@PathVariable Integer moduleId,
                                               @PathVariable Integer lessonId,
                                               @AuthenticationPrincipal User me) {
        Profiler profiler = new Profiler("completeLesson");
        profiler.start("progressService.complete");
        try {
            logger.info("Lesson completion requested by user: {} (moduleId={}, lessonId={})",
                    me.getUsername(), moduleId, lessonId);
            progressService.complete(moduleId, lessonId, me);
            logger.info("Lesson marked complete successfully: user={}, moduleId={}, lessonId={}",
                    me.getUsername(), moduleId, lessonId);
            return ResponseEntity.noContent().build();
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Un-complete a lesson ----------
    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}/complete")
    public ResponseEntity<Void> uncompleteLesson(@PathVariable Integer moduleId,
                                                 @PathVariable Integer lessonId,
                                                 @AuthenticationPrincipal User me) {
        Profiler profiler = new Profiler("uncompleteLesson");
        profiler.start("progressService.uncomplete");
        try {
            logger.info("Lesson un-completion requested by user: {} (moduleId={}, lessonId={})",
                    me.getUsername(), moduleId, lessonId);
            progressService.uncomplete(moduleId, lessonId, me);
            logger.info("Lesson marked un-complete successfully: user={}, moduleId={}, lessonId={}",
                    me.getUsername(), moduleId, lessonId);
            return ResponseEntity.noContent().build();
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Complete a quiz ----------
    @PostMapping("/modules/{moduleId}/quiz/{quizId}/complete")
    public ResponseEntity<Void> completeQuiz(@PathVariable Integer moduleId,
                                             @PathVariable Integer quizId,
                                             @RequestBody Map<Integer, Integer> answers,
                                             @AuthenticationPrincipal User me) {
        progressService.completeQuiz(moduleId, quizId, answers, me);
        return ResponseEntity.noContent().build();
    }

    // ---------- Un-complete a quiz ----------
    @DeleteMapping("/modules/{moduleId}/quiz/{quizId}/complete")
    public ResponseEntity<Void> uncompleteQuiz(@PathVariable Integer moduleId,
                                               @PathVariable Integer quizId,
                                               @AuthenticationPrincipal User me) {
        progressService.uncompleteQuiz(moduleId, quizId, me);
        return ResponseEntity.noContent().build();
    }
}
