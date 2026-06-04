package seniorhelper.repository;

import seniorhelper.entities.Lesson;
import seniorhelper.entities.LessonCompletion;
import seniorhelper.entities.Module;
import seniorhelper.entities.User;
import seniorhelper.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LessonCompletionRepositoryInMemoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    // ---------------------------------------------------------
    // helpers
    // ---------------------------------------------------------
    private User user(String username) {
        User u = new User();
        u.setUsername(username);
        u.setRole(Role.values()[0]);
        u.setPasswordHash("$2a$10$vlloF3RsRY9.bVuzEVXi1eMT1utDA9yz3IUATxcO2URWBtbmp2C0e");
        return em.persistFlushFind(u);
    }

    private Lesson lesson(String title) {
        Module m = new Module();
        m.setTitle("Test module for " + title);
        m = em.persistFlushFind(m);

        Lesson l = new Lesson();
        l.setTitle(title);
        l.setModule(m);
        return em.persistFlushFind(l);
    }

    private LessonCompletion completion(User u, Lesson l) {
        LessonCompletion c = new LessonCompletion();
        c.setUser(u);
        c.setLesson(l);
        return em.persistFlushFind(c);
    }

    // ---------------------------------------------------------
    // findAllByUser()
    // ---------------------------------------------------------
    @Test
    void findAllByUser_ReturnsOnlyThatUsersCompletions() {
        User alice = user("alice");
        User bob   = user("bob");

        Lesson l1 = lesson("Lesson 1");
        Lesson l2 = lesson("Lesson 2");

        completion(alice, l1);
        completion(alice, l2);
        completion(bob,   l1);

        List<LessonCompletion> result = lessonCompletionRepository.findAllByUser(alice);

        assertThat(result)
                .hasSize(2)
                .allMatch(c -> c.getUser().getId().equals(alice.getId()));
    }

    // ---------------------------------------------------------
    // existsByUserAndLesson()
    // ---------------------------------------------------------
    @Test
    void existsByUserAndLesson_ReflectsWhetherCompletionExists() {
        User u = user("alice");
        Lesson l = lesson("Lesson 1");

        completion(u, l);

        boolean exists = lessonCompletionRepository.existsByUserAndLesson(u, l);

        assertThat(exists).isTrue();
    }

    // ---------------------------------------------------------
    // findByUserAndLesson()
    // ---------------------------------------------------------
    @Test
    void findByUserAndLesson_ReturnsOptionalCompletion() {
        User u = user("alice");
        Lesson l = lesson("Lesson 1");

        LessonCompletion c = completion(u, l);

        Optional<LessonCompletion> found =
                lessonCompletionRepository.findByUserAndLesson(u, l);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(c.getId());
    }
}
