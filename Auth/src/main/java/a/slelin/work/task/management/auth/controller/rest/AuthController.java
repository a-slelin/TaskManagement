package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.AuthService;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    private final AuthService authService;

    @GetMapping({"", "/"})
    public Map<String, Object> info() {
        return Map.of(
                "name", applicationName,
                "version", applicationVersion,
                "description", "Auth Server for Task Management System Web App",
                "timestamp", LocalDateTime.now().toString(),
                "links", Map.of(
                        "/auth/login", "Login in the system",
                        "/auth/register", "Register in the system",
                        "/auth/refresh", "Update access & refresh token",
                        "/auth/logout", "Logout from the system (current session)",
                        "/auth/logout/all", "Logout from the system (all sessions)"
                ));
    }

    @GetMapping({"/refresh", "/refresh/"})
    public JwtResponse refresh(@RequestHeader("Authorization") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @GetMapping({"/logout", "/logout/"})
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/logout/all", "/logout/all/"})
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String refreshToken) {
        authService.logoutAll(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping({"/login", "/login/"})
    public JwtResponse login(@RequestBody LoginRequest login) {
        return authService.login(login);
    }

    @PostMapping({"/register", "/register/"})
    public JwtResponse register(@RequestBody UserWD newUser) {
        return authService.register(newUser);
    }
}
