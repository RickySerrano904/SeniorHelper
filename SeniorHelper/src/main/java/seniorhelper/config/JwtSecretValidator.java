package seniorhelper.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class JwtSecretValidator {

    private static final String DEV_JWT_SECRET = "dev-jwt-secret-change-me-please-dev-jwt-secret";

    private final Environment environment;
    private final String jwtSecret;

    public JwtSecretValidator(
            Environment environment,
            @Value("${app.security.jwt.secret:dev-jwt-secret-change-me-please-dev-jwt-secret}") String jwtSecret) {
        this.environment = environment;
        this.jwtSecret = jwtSecret;
    }

    @PostConstruct
    public void validateProductionSecret() {
        if (!isProductionProfile()) {
            return;
        }

        if (jwtSecret == null || jwtSecret.isBlank() || DEV_JWT_SECRET.equals(jwtSecret)) {
            throw new IllegalStateException(
                    "Set app.security.jwt.secret or APP_SECURITY_JWT_SECRET before starting with a production profile");
        }
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("prod")
                        || profile.equalsIgnoreCase("production"));
    }
}
