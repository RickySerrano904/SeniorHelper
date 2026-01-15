package edu.fscj.cop3024c.seniorhelper.security;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public TokenAuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasText(header)) {
            // Accept both "Bearer <token>" and raw "<token>"
            token = header.startsWith("Bearer ") ? header.substring(7) : header.trim();
        }

        if (StringUtils.hasText(token)) {
            authService.findUserByToken(token).ifPresent((User user) -> {
                // Map your domain role to Spring Security authorities
                // e.g. Role.ADMIN -> "ROLE_ADMIN"
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (user.getRole() != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
                }

                var authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        chain.doFilter(request, response);
    }
}
