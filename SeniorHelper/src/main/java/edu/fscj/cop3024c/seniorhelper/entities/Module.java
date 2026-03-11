package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    // One Module has many Lessons
    @OneToMany(mappedBy = "module", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    // One Module has one Quiz
    @OneToOne(mappedBy = "module", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, orphanRemoval = true)
    private Quiz quiz;

    // Constructors
    public Module() {}
    public Module(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        if (lessons != null) {
            lessons.forEach(l -> l.setModule(this)); // keep back-ref consistent
        }
    }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        if (quiz != null) quiz.setModule(this);
    }
}