package a.slelin.work.task.management.controller.rest;

import a.slelin.work.task.management.dto.*;
import a.slelin.work.task.management.service.ProjectService;
import a.slelin.work.task.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
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

    private final ProjectService projectService;

    @GetMapping(consumes = "*/*")
    public SheetDto<UserRD> getUsers(@PageableDefault(sort = "id") Pageable pageable,
                                     @RequestParam(value = "projects", required = false) String projects,
                                     @RequestParam(value = "tasks", required = false) String tasks) {
        return service.getAll(pageable, projects != null, tasks != null);
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public UserRD getUser(@PathVariable UUID id,
                          @RequestParam(value = "projects", required = false) String projects,
                          @RequestParam(value = "tasks", required = false) String tasks) {
        return service.getById(id, projects != null, tasks != null);
    }

    @GetMapping(path = "/{id}/projects", consumes = "*/*")
    public SheetDto<ProjectRD> getUserProjects(@PageableDefault(sort = "id") Pageable pageable,
                                               @PathVariable UUID id,
                                               @RequestParam(value = "tasks", required = false) String tasks) {
        return service.getUserProjects(pageable, id, tasks != null);
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

    @PostMapping("/{id}/projects")
    public ResponseEntity<ProjectRD> createUserProject(@PathVariable UUID id,
                                                       @RequestBody ProjectWD project) {
        ProjectRD savedProject = projectService.create(id, project);
        @SuppressWarnings("DataFlowIssue")
        URI location = MvcUriComponentsBuilder
                .fromMethodName(ProjectController.class, "getProject", savedProject.id(), null)
                .build()
                .toUri();
        return ResponseEntity.created(location)
                .body(savedProject);
    }

    @PutMapping("/{id}")
    public UserRD updateUser(@PathVariable UUID id,
                             @RequestBody UserWD user) {
        return service.update(id, user);
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

    @DeleteMapping(value = "/{id}/projects",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteUserProjects(@PathVariable UUID id) {
        service.deleteUserProjects(id);
        return ResponseEntity.noContent().build();
    }
}
