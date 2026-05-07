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
@RequiredArgsConstructor
@RequestMapping(value = "/api/projects",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
public class ProjectController {

    private final ProjectService service;

    private final TaskService taskService;

    @GetMapping(consumes = "*/*")
    public SheetDto<ProjectRD> getProjects(@PageableDefault(sort = "name") Pageable pageable,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.getUserProjects(user, pageable);
    }

    @GetMapping(path = "/{project}", consumes = "*/*")
    public ProjectRD getProject(@PathVariable Long project,
                                @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.getUserProject(user, project);
    }

    @GetMapping(path = "/{project}/tasks", consumes = "*/*")
    public SheetDto<TaskRD> getProjectTasks(@PathVariable Long project,
                                            @PageableDefault(sort = "title") Pageable pageable,
                                            @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.getUserProjectTasks(user, project, pageable);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<ProjectRD> searchProjects(@RequestBody FilterChain filters,
                                              @PageableDefault(sort = "name") Pageable pageable,
                                              @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.searchUserProjects(user, filters, pageable);
    }

    @PostMapping
    public ResponseEntity<ProjectRD> createProject(@RequestBody ProjectWD newProject,
                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);

        ProjectRD savedProject = service.createUserProject(user, newProject);
        URI location = MvcUriComponentsBuilder
                .fromMethodName(ProjectController.class, "getProject", savedProject.id(), jwt)
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedProject);
    }

    @PostMapping("/{project}/tasks")
    public ResponseEntity<TaskRD> createTask(@PathVariable Long project,
                                             @RequestBody TaskWD newTask,
                                             @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);

        TaskRD savedTask = taskService.createUserTask(user, project, newTask);
        URI location = MvcUriComponentsBuilder
                .fromMethodName(TaskController.class, "getTask", savedTask.id(), jwt)
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedTask);
    }

    @PutMapping("/{project}")
    public ProjectRD updateProject(@PathVariable Long project,
                                   @RequestBody ProjectWD updProject,
                                   @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.updateUserProject(user, project, updProject);
    }

    @PatchMapping("/{project}")
    public ProjectRD patchProject(@PathVariable Long project,
                                  @RequestBody ProjectWD pthProject,
                                  @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.patchUserProject(user, project, pthProject);
    }

    @DeleteMapping(consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteProjects(@AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        service.deleteUserProjects(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteProject(@PathVariable Long project,
                                              @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        service.deleteUserProject(user, project);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}/tasks", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteProjectTasks(@PathVariable Long project,
                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        service.deleteUserProjectTasks(user, project);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
