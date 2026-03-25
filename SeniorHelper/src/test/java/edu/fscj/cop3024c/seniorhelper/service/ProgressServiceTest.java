package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.*;
import edu.fscj.cop3024c.seniorhelper.entities.Module;
import edu.fscj.cop3024c.seniorhelper.repository.LessonCompletionRepository;
import edu.fscj.cop3024c.seniorhelper.repository.LessonRepository;
import edu.fscj.cop3024c.seniorhelper.repository.ModuleRepository;
import edu.fscj.cop3024c.seniorhelper.repository.QuizCompletionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonCompletionRepository lessonCompletionRepository;

    @Mock
    private QuizCompletionRepository quizCompletionRepository;

    @InjectMocks
    private ProgressService progressService;

    // ---------------------------------------------------------
    // complete() Lesson
    // ---------------------------------------------------------
    @Test
    void complete_ShouldSaveLessonCompletion_WhenNotAlreadyCompleted() {
        // Given
        Integer moduleId = 10;
        Integer lessonId = 100;
        User user = new User();
        user.setId(1);

        Module module = new Module();
        module.setId(moduleId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setModule(module);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonCompletionRepository.existsByUserAndLesson(user, lesson)).thenReturn(false);
        when(lessonCompletionRepository.save(any(LessonCompletion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        progressService.complete(moduleId, lessonId, user);

        // Then
        ArgumentCaptor<LessonCompletion> captor = ArgumentCaptor.forClass(LessonCompletion.class);
        verify(lessonCompletionRepository, times(1)).save(captor.capture());

        LessonCompletion saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getLesson()).isEqualTo(lesson);
    }

    // ---------------------------------------------------------
    // uncomplete() Lesson
    // ---------------------------------------------------------
    @Test
    void uncomplete_ShouldDeleteLessonCompletion_WhenExists() {
        // Given
        Integer moduleId = 10;
        Integer lessonId = 100;
        User user = new User();
        user.setId(1);

        Module module = new Module();
        module.setId(moduleId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setModule(module);

        LessonCompletion completion = new LessonCompletion(user, lesson);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonCompletionRepository.findByUserAndLesson(user, lesson))
                .thenReturn(Optional.of(completion));

        // When
        progressService.uncomplete(moduleId, lessonId, user);

        // Then
        verify(lessonCompletionRepository, times(1)).delete(completion);
    }

    // ---------------------------------------------------------
    // completeQuiz() - Completing a quiz should replace an existing completion for that user and quiz
    //                  because a quiz completion should always reflect the latest attempt.
    // ---------------------------------------------------------
    @Test
    void completeQuiz_ShouldReplaceExistingCompletion_ForGivenUserAndQuiz() {
        // Given
        Integer moduleId = 10;
        Integer quizId = 300;
        User user = new User();
        user.setId(1);

        Answer correctAnswer = new Answer();
        correctAnswer.setId(500);
        correctAnswer.setCorrect(true);

        Question question = new Question();
        question.setId(1000);
        question.setAnswers(List.of(correctAnswer));

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setName("Safety Quiz");
        quiz.setQuestions(List.of(question));

        Module module = new Module();
        module.setId(moduleId);
        module.setQuiz(quiz);

        Map<Integer, Integer> userAnswers = new HashMap<>();
        userAnswers.put(1000, 500);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(quizCompletionRepository.save(any(QuizCompletion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        progressService.completeQuiz(moduleId, quizId, userAnswers, user);

        // Then
        verify(quizCompletionRepository, times(1)).deleteByUserAndQuiz(user, quiz);
        verify(quizCompletionRepository, times(1)).flush();

        ArgumentCaptor<QuizCompletion> captor = ArgumentCaptor.forClass(QuizCompletion.class);
        verify(quizCompletionRepository, times(1)).save(captor.capture());

        QuizCompletion saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getQuiz()).isEqualTo(quiz);

        assertThat(saved.getCorrectCount()).isEqualTo(1);
    }

    // ---------------------------------------------------------
    // uncompleteQuiz()
    // ---------------------------------------------------------
    @Test
    void uncompleteQuiz_ShouldDeleteCompletion() {
        // Given
        Integer moduleId = 10;
        Integer quizId = 300;
        User user = new User();
        user.setId(1);

        Quiz quiz = new Quiz();
        quiz.setId(quizId);

        Module module = new Module();
        module.setId(moduleId);
        module.setQuiz(quiz);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        // When
        progressService.uncompleteQuiz(moduleId, quizId, user);

        // Then
        verify(quizCompletionRepository, times(1)).deleteByUserAndQuiz(user, quiz);
    }
}
