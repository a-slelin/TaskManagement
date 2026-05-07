package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.UserService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.auth.RoleCollection;
import a.slelin.work.task.management.core.dto.auth.UserRD;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/users",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"})
public class UserAdminController {

    @Autowired
    private UserService service;

    @GetMapping(consumes = "*/*")
    public SheetDto<UserRD> getAllUsers(@PageableDefault(sort = "username") Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping(path = "/{user}", consumes = "*/*")
    public UserRD getUserById(@PathVariable UUID user) {
        return service.getById(user);
    }

    @GetMapping(path = "/factor/{factor}", consumes = "*/*")
    public UserRD getUserByFactor(@PathVariable String factor) {
        return service.getByFactor(factor);
    }

    @PostMapping(path = {"/search", "/filter"})
    public SheetDto<UserRD> searchUsers(@RequestBody FilterChain filters,
                                        @PageableDefault(sort = "username") Pageable pageable) {
        return service.search(filters, pageable);
    }

    @PostMapping
    public ResponseEntity<UserRD> createUser(@RequestBody UserWD user) {
        UserRD savedUser = service.create(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment(savedUser.id())
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(savedUser);
    }

    @PatchMapping("/{user}")
    public UserRD patchUser(@PathVariable UUID user,
                            @RequestBody UserWD ptcUser,
                            @AuthenticationPrincipal Jwt jwt) {
        UUID actor = extractUserId(jwt);
        return service.patch(user, actor, ptcUser);
    }

    @DeleteMapping(value = "/{user}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID user,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID actor = extractUserId(jwt);
        service.delete(user, actor);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{user}/grant", produces = "*/*")
    public ResponseEntity<Void> grantUser(@PathVariable UUID user,
                                          @RequestBody RoleCollection roles) {
        service.grant(user, roles);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{user}/grant/{role}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> grantUser(@PathVariable UUID user,
                                          @PathVariable String role) {
        service.grant(user, RoleCollection.of(role));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{user}/revoke", produces = "*/*")
    public ResponseEntity<Void> revokeUser(@PathVariable UUID user,
                                           @RequestBody RoleCollection roles,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID actor = extractUserId(jwt);
        service.revoke(user, actor, roles);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{user}/revoke/{role}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> revokeUser(@PathVariable UUID user,
                                           @PathVariable String role,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID actor = extractUserId(jwt);
        service.revoke(user, actor, RoleCollection.of(role));
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
