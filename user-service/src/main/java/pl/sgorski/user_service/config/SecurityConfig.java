package pl.sgorski.user_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import pl.sgorski.security.CustomJwtAuthenticationConverter;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/callback").permitAll()
                        .requestMatchers("/api/internal/**").hasAnyRole("SERVICE_TICKET", "SERVICE_NOTIFICATION")
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
