package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.model.ScamCheckerDto;
import edu.fscj.cop3024c.seniorhelper.service.ScamDetectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scam")
public class ScamController {

    private final ScamDetectorService scamDetector;

    public ScamController(ScamDetectorService scamDetector) {
        this.scamDetector = scamDetector;
    }

    // Scam Checker Endpoints
    @PostMapping("/check")
    public ResponseEntity<ScamCheckerDto> check(@RequestBody ScamCheckerDto dto) {
        // Check if dto is missing text, or is empty.
        if (dto.getText() == null || dto.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        // Process text and return an HTTP result.
        ScamCheckerDto result = scamDetector.checkScamConfidence(dto);
        return ResponseEntity.ok(result);
    }
}
