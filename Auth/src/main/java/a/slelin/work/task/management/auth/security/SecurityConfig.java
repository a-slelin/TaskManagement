package a.slelin.work.task.management.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/info", "/info/",
                                "/help", "/help/").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/", "/api", "/api/",
                                "/api/info", "/api/info/",
                                "/api/help", "/api/help/").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/images/**").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/users/**",
                                "/api/roles/**", "/api/tokens/**").hasRole("ADMIN"))

                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
