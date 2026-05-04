package seniorhelper.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(
        name = "quiz_completions",
        uniqueConstraints = @UniqueConstraint(name = "uq_quiz_user", columnNames = {"user_id", "quiz_id"})
)
public class QuizCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "correct_count")
    private Integer correctCount = 0;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt = Instant.now();

    public QuizCompletion() {}
    public QuizCompletion(User user, Quiz quiz, Integer correctCount) {
        this.user = user;
        this.quiz = quiz;
        this.correctCount = correctCount;
        this.completedAt = Instant.now();
    }

    public Integer getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
