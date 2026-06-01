package seniorhelper.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtSecretValidatorTest {

    @Test
    void validateProductionSecret_throwsWhenProductionUsesDefaultSecret() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        JwtSecretValidator validator = new JwtSecretValidator(
                environment,
                "dev-jwt-secret-change-me-please-dev-jwt-secret");

        assertThatThrownBy(validator::validateProductionSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("APP_SECURITY_JWT_SECRET");
    }

    @Test
    void validateProductionSecret_allowsProductionWithCustomSecret() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("production");
        JwtSecretValidator validator = new JwtSecretValidator(
                environment,
                "replace-with-a-long-random-secret-from-env");

        assertThatCode(validator::validateProductionSecret).doesNotThrowAnyException();
    }

    @Test
    void validateProductionSecret_allowsDefaultSecretOutsideProduction() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev");
        JwtSecretValidator validator = new JwtSecretValidator(
                environment,
                "dev-jwt-secret-change-me-please-dev-jwt-secret");

        assertThatCode(validator::validateProductionSecret).doesNotThrowAnyException();
    }
}
