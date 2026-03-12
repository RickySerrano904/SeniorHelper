package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_id", nullable = false)
    private Integer id;

    @NotBlank
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "\"end\"")
    private LocalDateTime end;

    @Column(name = "location", length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "senior_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointments_senior")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User senior;

    public Appointment() { }

    public Appointment(String title, String notes, LocalDateTime start, LocalDateTime end, String location) {
        this.title = title;
        this.notes = notes;
        this.start = start;
        this.end = end;
        this.location = location;
    }

    public Appointment(String title, String notes, LocalDateTime start, LocalDateTime end, String location, User senior) {
        this(title, notes, start, end, location);
        this.senior = senior;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public User getSenior() { return senior; }
    public void setSenior(User senior) { this.senior = senior; }
}
