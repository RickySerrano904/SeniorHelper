package seniorhelper.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ModuleDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Module title is required")
    private String title;

    private String description;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<LessonDto> lessons;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private QuizDto quiz;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<LessonDto> getLessons() { return lessons; }
    public void setLessons(List<LessonDto> lessons) { this.lessons = lessons; }

    public QuizDto getQuiz() { return quiz; }
    public void setQuiz(QuizDto quiz) { this.quiz = quiz; }
}