package a.slelin.work.task.management.controller.rest;

import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.TaskService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/tasks",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping(consumes = "*/*")
    public List<TaskRD> getTasks() {
        return service.getAll();
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public TaskRD getTask(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @PutMapping(path = "/{id}")
    public TaskRD updateTask(@PathVariable @Min(1) Long id,
                             @RequestBody TaskWD task) {
        return service.update(id, task);
    }

    @PatchMapping(path = "/{id}")
    public TaskRD patchTask(@PathVariable @Min(1) Long id,
                            @RequestBody TaskWD task) {
        return service.patch(id, task);
    }

    @PostMapping(path = "/{id}/project/{projectId}", consumes = "*/*")
    public TaskRD setProject(@PathVariable @Min(1) Long id,
                             @PathVariable @Min(1) Long projectId) {
        return service.drawToProject(projectId, id);
    }

    @DeleteMapping(path = "/{id}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteTask(@PathVariable @Min(1) Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
