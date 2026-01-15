package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.model.ScamCheckerDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScamDetectorService {

    private static final List<String> REWARD = List.of("congratulations", "free", "win", "won", "winner", "gift",
            "reward", "prize", "contest", "giveaway", "free trial", "claim your prize", "selected", "limited offer");
    private static final List<String> URGENT = List.of("urgent", "final notice", "limited time", "immediately", "act quickly",
            "act now", "deadline", "last chance");
    private static final List<String> THREAT = List.of("avoid penalties", "suspend", "close your account", "legal action",
            "lawsuit", "fine", "collection notice", "fraudulent", "final warning");
    private static final List<String> PERSONAL = List.of("account information", "verify", "account", "bank", "password",
            "social security", "ssn", "unlock", "credentials", "access", "update your information");
    private static final List<String> IMPERSONATE = List.of("medicare", "irs", "fbi", "social security administration",
            "government", "police", "representative", "agent", "refund department", "tech support", "microsoft support", "microsoft account");
    private static final List<String> PAYMENT = List.of("paypal", "venmo", "zelle", "cashapp", "bitcoin",
            "google play card", "itunes card", "gift card", "prepaid card", "money order", "cash", "cryptocurrency", "crypto");
    private static final List<String> DELIVERY = List.of("usps", "ups", "fedex", "confirm delivery", "reschedule delivery",
            "delivery issue", "delivery failed");
    private static final List<String> FAMILY = List.of("grandson", "granddaughter", "relative", "family member", "emergency",
            "need money", "hospital", "bail money", "accident");

    // Method to check scams, determine verdict, and system confidence in verdict.
    public ScamCheckerDto checkScamConfidence(ScamCheckerDto dto) {

        String text = dto.getText().toLowerCase(Locale.ROOT);
        List<String> detectedWords = new ArrayList<>();
        int scamScore = 0;

        // Match user-provided text to string lists.
        for(String word: REWARD) if (text.contains(word)) { scamScore += 2; detectedWords.add(word); }
        for(String word: URGENT) if (text.contains(word)) { scamScore += 2; detectedWords.add(word); }
        for(String word: DELIVERY) if (text.contains(word)) { scamScore += 2; detectedWords.add(word); }
        for(String word: THREAT) if (text.contains(word)) { scamScore += 3; detectedWords.add(word); }
        for(String word: PERSONAL) if (text.contains(word)) { scamScore += 3; detectedWords.add(word); }
        for(String word: FAMILY) if (text.contains(word)) { scamScore += 3; detectedWords.add(word); }
        for(String word: IMPERSONATE) if (text.contains(word)) { scamScore += 4; detectedWords.add(word); }
        for(String word: PAYMENT) if (text.contains(word)) { scamScore += 4; detectedWords.add(word); }

        String verdict;             // Creates a String verdict to display to the user.
        double systemConfidence;    // Determines level of confidence based on the total scam score. Levels: 20%, 50%, 80%

        if (scamScore >= 10) { verdict = "Likely Scam"; systemConfidence = 0.8; }
        else if (scamScore >= 5) { verdict = "Suspicious"; systemConfidence = 0.5; }
        else { verdict = "Seemingly Safe"; systemConfidence = 0.2; }

        dto.setVerdict(verdict);
        dto.setConfidenceRating(systemConfidence);
        dto.setDetectedPattern(detectedWords);

        return dto;
    }
}
