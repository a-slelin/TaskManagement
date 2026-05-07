package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.UserService;
import a.slelin.work.task.management.core.dto.auth.UserRD;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"})
public class UserController {

    private final UserService service;

    @GetMapping(consumes = "*/*")
    public UserRD getUser(@AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.getById(user);
    }

    @PatchMapping
    public UserRD patchUser(@RequestBody UserWD pthUser,
                            @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.patch(user, user, pthUser);
    }

    @DeleteMapping(consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        service.delete(user, user);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
