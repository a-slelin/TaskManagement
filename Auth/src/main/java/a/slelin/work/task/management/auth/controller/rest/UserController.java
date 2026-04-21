package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.service.UserService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.UserRD;
import a.slelin.work.task.management.core.dto.api.UserWD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/api/users",
        consumes = {"application/json", "application/xml", "application/yaml"},
        produces = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping(consumes = "*/*")
    public SheetDto<UserRD> getUsers(@PageableDefault(sort = "id") Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public UserRD getUser(@PathVariable UUID id) {
        return service.getById(id);
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

    @PostMapping({"/search", "/filter"})
    public SheetDto<UserRD> searchUsers(@PageableDefault(sort = "id") Pageable pageable,
                                        @RequestBody FilterChain filters) {
        return service.search(pageable, filters);
    }

    @PatchMapping("/{id}")
    public UserRD patchUser(@PathVariable UUID id,
                            @RequestBody UserWD user) {
        return service.patch(id, user);
    }

    @DeleteMapping(value = "/{id}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
