package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "care_links",
        uniqueConstraints = @UniqueConstraint(columnNames = {"caregiver_id", "senior_id"})
)
public class CareLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private User caregiver;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;

    @CreationTimestamp
    @Column(name = "connected_since", updatable = false)
    private LocalDateTime connectedSince;

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getCaregiver() { return caregiver; }
    public void setCaregiver(User caregiver) { this.caregiver = caregiver; }

    public User getSenior() { return senior; }
    public void setSenior(User senior) { this.senior = senior; }

    public LocalDateTime getConnectedSince() { return connectedSince; }
    public void setConnectedSince(LocalDateTime connectedSince) { this.connectedSince = connectedSince; }
}
