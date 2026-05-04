package seniorhelper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class LessonDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String description;

    private List<Object> contentBlocks;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Object> getContentBlocks() { return contentBlocks; }
    public void setContentBlocks(List<Object> contentBlocks) { this.contentBlocks = contentBlocks; }
}