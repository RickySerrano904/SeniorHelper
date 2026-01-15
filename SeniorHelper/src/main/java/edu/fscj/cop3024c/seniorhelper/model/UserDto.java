package edu.fscj.cop3024c.seniorhelper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be 3-50 characters")
    private String username;

    // SENIOR, CAREGIVER, FAMILY, ADMIN
    private String role;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 100, message = "password must be at least 8 characters")
    private String password;

    // === Constructors ===
    public UserDto() {}

    public UserDto(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
        //this.password = password;
    }



    // === Getters and Setters ===
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
