package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Lesson;
import edu.fscj.cop3024c.seniorhelper.entities.LessonCompletion;
import edu.fscj.cop3024c.seniorhelper.entities.Module;
import edu.fscj.cop3024c.seniorhelper.entities.Quiz;
import edu.fscj.cop3024c.seniorhelper.entities.QuizCompletion;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.ProgressDto;
import edu.fscj.cop3024c.seniorhelper.repository.LessonCompletionRepository;
import edu.fscj.cop3024c.seniorhelper.repository.LessonRepository;
import edu.fscj.cop3024c.seniorhelper.repository.ModuleRepository;
import edu.fscj.cop3024c.seniorhelper.repository.QuizCompletionRepository;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final QuizCompletionRepository quizCompletionRepository;
    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;

    public ProgressService(ModuleRepository moduleRepository,
                           LessonRepository lessonRepository,
                           LessonCompletionRepository lessonCompletionRepository,
                           QuizCompletionRepository quizCompletionRepository,
                           UserRepository userRepository,
                           PermissionChecker permissionChecker) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.lessonCompletionRepository = lessonCompletionRepository;
        this.quizCompletionRepository = quizCompletionRepository;
        this.userRepository = userRepository;
        this.permissionChecker = permissionChecker;
    }

    // Read overall progress (for a specific User entity)
    @Transactional(readOnly = true)
    public ProgressDto overall(User user) {
        List<Module> allModules = moduleRepository.findAll();

        // Completed lesson IDs for this user
        Set<Integer> completedLessonIds = lessonCompletionRepository.findAllByUser(user).stream()
                .map(c -> c.getLesson().getId())
                .collect(Collectors.toSet());

        List<QuizCompletion> allCompletions = quizCompletionRepository.findAllByUser(user);

        List<ProgressDto.Module> moduleDtos = new ArrayList<>();

        for (Module m : allModules) {
            List<Lesson> lessons = (m.getLessons() != null) ? m.getLessons() : List.of();

            var lessonDtos = lessons.stream()
                    .map(lsn -> new ProgressDto.Lesson(
                            lsn.getId(),
                            lsn.getTitle(),
                            completedLessonIds.contains(lsn.getId())
                    ))
                    .toList();

            ProgressDto.Module mod = new ProgressDto.Module();
            mod.setId(m.getId());
            mod.setTitle(m.getTitle());
            mod.setDescription(m.getDescription());
            mod.setLessons(lessonDtos);

            // Module's single quiz (if present)
            Quiz quiz = m.getQuiz();
            if (quiz != null) {
                QuizCompletion qc = allCompletions.stream()
                        .filter(c -> c.getQuiz().getId().equals(quiz.getId()))
                        .findFirst().orElse(null);

                boolean done = (qc != null);
                Integer correct = done ? qc.getCorrectCount() : 0;
                Integer total = (quiz.getQuestions() != null) ? quiz.getQuestions().size() : 0;

                mod.setQuiz(new ProgressDto.Quiz(quiz.getId(), quiz.getName(), done, correct, total));
            } else {
                mod.setQuiz(null);
            }
            moduleDtos.add(mod);
        }

        ProgressDto dto = new ProgressDto();
        dto.setUserId(user.getId());
        dto.setModules(moduleDtos);
        return dto;
    }

    // Admin/caregiver can request any senior's progress (with permission check)
    @Transactional(readOnly = true)
    public ProgressDto overallForSenior(Integer seniorId, User requester) {
        if (requester == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'User' (senior) ID: " + seniorId));

        if (!permissionChecker.hasPermission(requester, seniorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this senior's progress");
        }
        return overall(senior);
    }

    // LESSONS ==============================================

    @Transactional
    public void complete(Integer moduleId, Integer lessonId, User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Module' ID: " + moduleId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Lesson' ID: " + lessonId));

        if (lesson.getModule() == null || !Objects.equals(lesson.getModule().getId(), module.getId())) {
            throw new NotFoundException("Lesson " + lessonId + " is not part of Module " + moduleId);
        }

        if (!lessonCompletionRepository.existsByUserAndLesson(user, lesson)) {
            lessonCompletionRepository.save(new LessonCompletion(user, lesson));
        }
    }

    @Transactional
    public void uncomplete(Integer moduleId, Integer lessonId, User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Module' ID: " + moduleId));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Lesson' ID: " + lessonId));

        if (lesson.getModule() == null || !Objects.equals(lesson.getModule().getId(), module.getId())) {
            throw new NotFoundException("Lesson " + lessonId + " is not part of Module " + moduleId);
        }

        lessonCompletionRepository.findByUserAndLesson(user, lesson)
                .ifPresent(lessonCompletionRepository::delete);
    }

    // QUIZZES ======================================================

    @Transactional
    public void completeQuiz(Integer moduleId, Integer quizId, Map<Integer, Integer> answers, User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Module' ID: " + moduleId));

        Quiz quiz = Optional.ofNullable(module.getQuiz())
                .filter(q -> Objects.equals(q.getId(), quizId))
                .orElseThrow(() -> new NotFoundException("Quiz " + quizId + " is not associated with Module " + moduleId));

        int correctCount = 0;
        if (quiz.getQuestions() != null && answers != null) {
            for (var question : quiz.getQuestions()) {
                Integer selectedAnswerId = answers.get(question.getId());

                boolean isCorrect = question.getAnswers().stream()
                        .anyMatch(a -> a.getId().equals(selectedAnswerId) && a.isCorrect());

                if (isCorrect) {
                    correctCount++;
                }
            }
        }

        // Delete any stray/duplicate rows first, then insert exactly one
        quizCompletionRepository.deleteByUserAndQuiz(user, quiz);
        quizCompletionRepository.flush();
        quizCompletionRepository.save(new QuizCompletion(user, quiz, correctCount));
    }

    @Transactional
    public void uncompleteQuiz(Integer moduleId, Integer quizId, User user) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Unable to locate 'Module' ID: " + moduleId));

        Quiz quiz = Optional.ofNullable(module.getQuiz())
                .filter(q -> Objects.equals(q.getId(), quizId))
                .orElseThrow(() -> new NotFoundException("Quiz " + quizId + " is not associated with Module " + moduleId));

        quizCompletionRepository.deleteByUserAndQuiz(user, quiz);
    }
}
