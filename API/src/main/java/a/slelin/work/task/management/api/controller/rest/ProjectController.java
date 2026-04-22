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
    public SheetDto<ProjectRD> getProjects(@PageableDefault(sort = "name") Pageable pageable) {
        UUID user = getUserFromSecurityContext();
        return service.getUserProjects(user, pageable);
    }

    @GetMapping(path = "/{project}", consumes = "*/*")
    public ProjectRD getProject(@PathVariable Long project) {
        UUID user = getUserFromSecurityContext();
        return service.getUserProject(user, project);
    }

    @GetMapping(path = "/{project}/tasks", consumes = "*/*")
    public SheetDto<TaskRD> getProjectTasks(@PageableDefault(sort = "title") Pageable pageable,
                                            @PathVariable Long project) {
        UUID user = getUserFromSecurityContext();
        return service.getUserProjectTasks(user, project, pageable);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<ProjectRD> searchProjects(@PageableDefault(sort = "name") Pageable pageable,
                                              @RequestBody FilterChain filters) {
        UUID user = getUserFromSecurityContext();
        return service.searchUserProjects(user, filters, pageable);
    }

    @PostMapping
    public ResponseEntity<ProjectRD> createProject(@RequestBody ProjectWD newProject) {
        UUID user = getUserFromSecurityContext();

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
    public ResponseEntity<TaskRD> createTask(@PathVariable Long project,
                                             @RequestBody TaskWD newTask) {
        UUID user = getUserFromSecurityContext();

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
    public ProjectRD updateProject(@PathVariable Long project,
                                   @RequestBody ProjectWD updProject) {
        UUID user = getUserFromSecurityContext();
        return service.updateUserProject(user, project, updProject);
    }

    @PatchMapping("/{project}")
    public ProjectRD patchProject(@PathVariable Long project,
                                  @RequestBody ProjectWD pthProject) {
        UUID user = getUserFromSecurityContext();
        return service.patchUserProject(user, project, pthProject);
    }

    @DeleteMapping(consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteProjects() {
        UUID user = getUserFromSecurityContext();

        service.deleteUserProjects(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProject(@PathVariable Long project) {
        UUID user = getUserFromSecurityContext();

        service.deleteUserProject(user, project);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{project}/tasks",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProjectTasks(@PathVariable Long project) {
        UUID user = getUserFromSecurityContext();

        service.deleteUserProjectTasks(user, project);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserFromSecurityContext() {
        return UUID.randomUUID();
    }
}
