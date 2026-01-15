package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    // Many Lessons belong to One Module
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonCompletion> completions = new ArrayList<>();

    public List<LessonCompletion> getCompletions() { return completions; }
    public void setCompletions(List<LessonCompletion> completions) {
        this.completions = completions;
    }

    // Constructors
    public Lesson() {}
    public Lesson(String title, String description, Module module) {
        this.title = title;
        this.description = description;
        this.module = module;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
}