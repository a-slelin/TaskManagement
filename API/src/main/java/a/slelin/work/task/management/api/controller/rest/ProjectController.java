package a.slelin.work.task.management.api.controller.rest;

import a.slelin.work.task.management.api.service.ProjectService;
import a.slelin.work.task.management.api.service.TaskService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/projects",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    private final TaskService taskService;

    @GetMapping(consumes = "*/*")
    public SheetDto<ProjectRD> getProjects(@AuthenticationPrincipal Jwt jwt,
                                           @PageableDefault(sort = "name") Pageable pageable) {
        return service.getUserProjects(extractUserId(jwt), pageable);
    }

    @GetMapping(path = "/{project}", consumes = "*/*")
    public ProjectRD getProject(@AuthenticationPrincipal Jwt jwt,
                                @PathVariable Long project) {
        return service.getUserProject(extractUserId(jwt), project);
    }

    @GetMapping(path = "/{project}/tasks", consumes = "*/*")
    public SheetDto<TaskRD> getProjectTasks(@AuthenticationPrincipal Jwt jwt,
                                            @PageableDefault(sort = "title") Pageable pageable,
                                            @PathVariable Long project) {
        return service.getUserProjectTasks(extractUserId(jwt), project, pageable);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<ProjectRD> searchProjects(@AuthenticationPrincipal Jwt jwt,
                                              @PageableDefault(sort = "name") Pageable pageable,
                                              @RequestBody FilterChain filters) {
        return service.searchUserProjects(extractUserId(jwt), filters, pageable);
    }

    @PostMapping
    public ResponseEntity<ProjectRD> createProject(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody ProjectWD newProject) {
        UUID user = extractUserId(jwt);

        ProjectRD savedProject = service.createUserProject(user, newProject);
        URI location = MvcUriComponentsBuilder
                .fromMethodName(ProjectController.class, "getProject", savedProject.id())
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedProject);
    }

    @PostMapping("/{project}/tasks")
    public ResponseEntity<TaskRD> createTask(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable Long project,
                                             @RequestBody TaskWD newTask) {
        UUID user = extractUserId(jwt);

        TaskRD savedTask = taskService.createUserTask(user, project, newTask);
        URI location = MvcUriComponentsBuilder
                .fromMethodName(TaskController.class, "getTask", savedTask.id())
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedTask);
    }

    @PutMapping("/{project}")
    public ProjectRD updateProject(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long project,
                                   @RequestBody ProjectWD updProject) {
        return service.updateUserProject(extractUserId(jwt), project, updProject);
    }

    @PatchMapping("/{project}")
    public ProjectRD patchProject(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable Long project,
                                  @RequestBody ProjectWD pthProject) {
        return service.patchUserProject(extractUserId(jwt), project, pthProject);
    }

    @DeleteMapping(consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteProjects(@AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);

        service.deleteUserProjects(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long project) {
        UUID user = extractUserId(jwt);

        service.deleteUserProject(user, project);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}/tasks",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProjectTasks(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Long project) {
        UUID user = extractUserId(jwt);

        service.deleteUserProjectTasks(user, project);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
