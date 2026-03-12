package edu.fscj.cop3024c.seniorhelper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class AppointmentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;


    private String title;
    private String notes;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;

    public AppointmentDto(Integer id,  String title, String notes,
                          String location, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.start = start;
        this.end = end;
    }

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
}
