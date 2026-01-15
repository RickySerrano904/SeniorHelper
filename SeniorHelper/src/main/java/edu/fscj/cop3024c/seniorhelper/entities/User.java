package edu.fscj.cop3024c.seniorhelper.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users") // avoid 'user' in postgres
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @Column(name = "salt")
    private String salt;

    @JsonIgnore
    @Column(name = "hash")
    private String hash;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<CareLink> caregiverLinks = new ArrayList<>();

    @OneToMany(mappedBy = "senior", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<CareLink> seniorLinks = new ArrayList<>();

    // Constructors
    public User() {}

    public User(String username) {
        this.username = username;
    }

    @OneToMany(mappedBy = "senior")
    private Set<Appointment> appointments = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    // Getters & setters
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Set<Appointment> getAppointments() { return appointments; }
    public void setAppointments(Set<Appointment> appointments) { this.appointments = appointments; }
}