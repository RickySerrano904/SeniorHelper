package edu.fscj.cop3024c.seniorhelper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Caregiver-to-Senior link")
public class CareLinkDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotNull(message = "CaregiverId must not be null")
    private Integer caregiverId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String caregiverFirstName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String caregiverLastName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String caregiverUsername;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String caregiverRole;

    @NotNull(message = "SeniorId must not be null")
    private Integer seniorId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String seniorFirstName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String seniorLastName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String seniorUsername;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String seniorRole;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime connectedSince;

    public CareLinkDto() {}

    public CareLinkDto(Integer id,
                       Integer caregiverId, String caregiverUsername, String caregiverRole,
                       String caregiverFirstName, String caregiverLastName,
                       Integer seniorId, String seniorUsername, String seniorRole,
                       String seniorFirstName, String seniorLastName,
                       LocalDateTime connectedSince) {
        this.id = id;
        this.caregiverId = caregiverId;
        this.caregiverUsername = caregiverUsername;
        this.caregiverFirstName = caregiverFirstName;
        this.caregiverLastName = caregiverLastName;
        this.caregiverRole = caregiverRole;
        this.seniorId = seniorId;
        this.seniorUsername = seniorUsername;
        this.seniorRole = seniorRole;
        this.seniorFirstName = seniorFirstName;
        this.seniorLastName = seniorLastName;
        this.connectedSince = connectedSince;
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCaregiverId() { return caregiverId; }
    public void setCaregiverId(Integer caregiverId) { this.caregiverId = caregiverId; }

    public String getCaregiverFirstName() { return caregiverFirstName; }
    public void setCaregiverFirstName(String name) { this.caregiverFirstName = name; }

    public String getCaregiverLastName() { return caregiverLastName; }
    public void setCaregiverLastName(String name) { this.caregiverLastName = name; }

    public String getCaregiverUsername() { return caregiverUsername; }
    public void setCaregiverUsername(String caregiverUsername) { this.caregiverUsername = caregiverUsername; }

    public String getCaregiverRole() { return caregiverRole; }
    public void setCaregiverRole(String caregiverRole) { this.caregiverRole = caregiverRole; }

    public Integer getSeniorId() { return seniorId; }
    public void setSeniorId(Integer seniorId) { this.seniorId = seniorId; }

    public String getSeniorUsername() { return seniorUsername; }
    public void setSeniorUsername(String seniorUsername) { this.seniorUsername = seniorUsername; }

    public String getSeniorRole() { return seniorRole; }
    public void setSeniorRole(String seniorRole) { this.seniorRole = seniorRole; }

    public String getSeniorFirstName() { return seniorFirstName; }
    public void setSeniorFirstName(String name) { this.seniorFirstName = name; }

    public LocalDateTime getConnectedSince() { return connectedSince; }
    public void setConnectedSince(LocalDateTime connectedSince) { this.connectedSince = connectedSince; }
}