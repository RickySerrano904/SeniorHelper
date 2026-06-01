package seniorhelper.config;

import seniorhelper.error.ApiError;
import seniorhelper.security.TokenAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final Environment environment;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] PUBLIC_API = {
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/health"
    };

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenAuthFilter tokenAuthFilter) throws Exception {
        var mapper = new ObjectMapper();
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                if (isSwaggerPublic()) {
                    auth.requestMatchers(SWAGGER_WHITELIST).permitAll();
                } else {
                    auth.requestMatchers(SWAGGER_WHITELIST).authenticated();
                }
                auth.requestMatchers(PUBLIC_API).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll();
            })
            .exceptionHandling(ex -> ex
                    // 401 for unauthenticated access
                    .authenticationEntryPoint((req, res, e) -> {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        var body = new ApiError(req.getRequestURI(),
                                "Unauthorized: " + e.getMessage(), 401);
                        res.getWriter().write(mapper.writeValueAsString(body));
                    })
                    // 403 for authenticated but forbidden
                    .accessDeniedHandler((req, res, e) -> {
                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        res.setContentType("application/json");
                        var body = new ApiError(req.getRequestURI(),
                                "Forbidden: " + e.getMessage(), 403);
                        res.getWriter().write(mapper.writeValueAsString(body));
                    })
            )
            .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    boolean isSwaggerPublic() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("dev"));
    }
}
