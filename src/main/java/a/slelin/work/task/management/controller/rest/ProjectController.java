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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/projects",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    private final TaskService taskService;

    @GetMapping(consumes = "*/*")
    public List<ProjectRD> getProjects(@RequestParam(value = "tasks", required = false) String tasks) {
        return service.getAll(tasks != null);
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public ProjectRD getProject(@PathVariable @Min(1) Long id,
                                @RequestParam(value = "tasks", required = false) String tasks) {
        return service.getById(id, tasks != null);
    }

    @GetMapping(path = "/{id}/tasks", consumes = "*/*")
    public List<TaskRD> getProjectTasks(@PathVariable @Min(1) Long id) {
        return service.getProjectTasks(id);
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskRD> createTask(@PathVariable @Min(1) Long id,
                                             @RequestBody TaskWD task) {
        TaskRD savedTask = taskService.create(id, task);
        URI location = MvcUriComponentsBuilder
                .fromMethodName(TaskController.class, "getTask", savedTask.id())
                .build()
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedTask);
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
