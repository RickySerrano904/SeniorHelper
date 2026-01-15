package edu.fscj.cop3024c.seniorhelper.model;

import java.util.List;

public class ScamCheckerDto {
    // Potentially add an @NotBlank(message = "") annotation to not allow blank text.
    private String text;
    private String verdict;
    private Double confidenceRating;
    private List<String> detectedPattern;

    // Constructor
    public ScamCheckerDto(String text, String verdict, Double confidenceRating, List<String> detectedPattern) {
        this.text = text;
        this.verdict = verdict;
        this.confidenceRating = confidenceRating;
        this.detectedPattern = detectedPattern;
    }

    // Getters & Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public double getConfidenceRating() { return confidenceRating; }
    public void setConfidenceRating(Double confidenceRating) { this.confidenceRating = confidenceRating; }

    public List<String> getDetectedPattern() { return detectedPattern; }
    public void setDetectedPattern(List<String> detectedPattern) { this.detectedPattern = detectedPattern; }
}