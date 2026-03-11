package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;

    // Many questions belong to one quiz.
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // One question can have many answers.
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Answer> answers = new ArrayList<>();

    // Constructors
    public Question() {}
    public Question(String text, Quiz quiz) {
        this.text = text;
        this.quiz = quiz;
    }
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}