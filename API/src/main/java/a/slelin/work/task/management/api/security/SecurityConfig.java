package a.slelin.work.task.management.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private JwtDecoder jwtDecoder;

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/", "/help", "/help/",
                                "/info", "/info/", "/api", "/api/",
                                "/api/help", "/api/help/",
                                "/api/info", "/api/info/").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/images/**").permitAll())

                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/projects/**",
                                "/api/tasks/**").hasRole("USER"))

                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))

                .build();
    }
}
