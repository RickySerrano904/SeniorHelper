package seniorhelper.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void isSwaggerPublic_returnsTrueForDevProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev");

        assertThat(new SecurityConfig(environment).isSwaggerPublic()).isTrue();
    }

    @Test
    void isSwaggerPublic_returnsTrueForDefaultDevProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setDefaultProfiles("dev");

        assertThat(new SecurityConfig(environment).isSwaggerPublic()).isTrue();
    }

    @Test
    void isSwaggerPublic_returnsFalseForProductionProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("production");

        assertThat(new SecurityConfig(environment).isSwaggerPublic()).isFalse();
    }

    @Test
    void isSwaggerPublic_returnsFalseWhenNoProfileIsActive() {
        MockEnvironment environment = new MockEnvironment();

        assertThat(new SecurityConfig(environment).isSwaggerPublic()).isFalse();
    }
}
