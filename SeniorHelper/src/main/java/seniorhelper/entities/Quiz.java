package seniorhelper.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    // One Quiz belongs to one Module
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id")
    private Module module;

    // One quiz has many questions.
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<QuizCompletion> completions = new ArrayList<>();

    // Constructors
    public Quiz() {}
    public Quiz(String name, Module module) {
        this.name = name;
        this.module = module;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<QuizCompletion> getCompletions() { return completions; }
    public void setCompletions(List<QuizCompletion> completions) { this.completions = completions; }
}