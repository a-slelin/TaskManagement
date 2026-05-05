package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.AuthService;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"})
public class AuthController {

    private final AuthService authService;

    @GetMapping(value = {"/refresh", "/refresh/"}, consumes = "*/*")
    public JwtResponse refresh(@RequestHeader("Authorization") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @GetMapping(value = {"/logout", "/logout/"}, consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = {"/logout/all", "/logout/all/"}, consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String refreshToken) {
        authService.logoutAll(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = {"/login", "/login/"})
    public JwtResponse login(@RequestBody LoginRequest login) {
        return authService.login(login);
    }

    @PostMapping({"/register", "/register/"})
    public JwtResponse register(@RequestBody UserWD newUser) {
        return authService.register(newUser);
    }
}
