package a.slelin.work.task.management.controller.rest;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.ProjectService;
import a.slelin.work.task.management.service.TaskService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Validated
@RestController
@RequestMapping(value = "/api/projects",
        produces = "application/json",
        consumes = "application/json")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    private final TaskService taskService;

    @GetMapping(consumes = "*/*")
    public List<ProjectRD> getProjects(@RequestParam(value = "tasks", defaultValue = "false") boolean tasks) {
        return service.getAll(tasks);
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public ProjectRD getProject(@PathVariable @Min(1) Long id,
                                @RequestParam(value = "tasks", defaultValue = "false") boolean tasks) {
        return service.getById(id, tasks);
    }

    @GetMapping(path = "/{id}/tasks", consumes = "*/*")
    public List<TaskRD> getProjectTasks(@PathVariable @Min(1) Long id) {
        return service.getProjectTasks(id);
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskRD> createTask(@PathVariable @Min(1) Long id,
                                             @RequestBody TaskWD task) {
        TaskRD body = taskService.create(id, task);
        URI location = fromMethodCall(on(TaskController.class).getTask(body.id()))
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(body);
    }

    @PutMapping("/{id}")
    public ProjectRD updateProject(@PathVariable @Min(1) Long id,
                                   @RequestBody ProjectWD project) {
        return service.update(id, project);
    }

    @PatchMapping("/{id}")
    public ProjectRD patchProject(@PathVariable @Min(1) Long id,
                                  @RequestBody ProjectWD project) {
        return service.patch(id, project);
    }

    @DeleteMapping(path = "/{id}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProject(@PathVariable @Min(1) Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/tasks",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteProjectTasks(@PathVariable @Min(1) Long id) {
        service.deleteTasks(id);
        return ResponseEntity.noContent().build();
    }
}
