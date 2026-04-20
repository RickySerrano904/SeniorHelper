package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;

@Entity(name = "SeniorReview")  // ✅ FIX: unique entity name
@Table(name = "reviews")       // optional but recommended for DB clarity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String comment;
    private int rating;

    public Review() {
    }

    public Review(Long id, String username, String comment, int rating) {
        this.id = id;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}