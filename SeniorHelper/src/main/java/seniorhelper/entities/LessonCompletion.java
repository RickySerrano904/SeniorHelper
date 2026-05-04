package seniorhelper.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.Instant;

@Entity
@Table(
        name = "lesson_completions",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_lesson", columnNames = {"user_id", "lesson_id"})
)

public class LessonCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lesson lesson;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt = Instant.now();

    public LessonCompletion() { }

    public LessonCompletion(User user, Lesson lesson) {
        this.user = user;
        this.lesson = lesson;
        this.completedAt = Instant.now();
    }

    public Integer getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}