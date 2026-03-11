package a.slelin.work.task.management.controller.rest;

import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.service.TaskService;
import a.slelin.work.task.management.util.filter.FilterChain;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/api/tasks",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping(consumes = "*/*")
    public SheetDto<TaskRD> getTasks(@PageableDefault(sort = "id") Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping(path = "/{id}", consumes = "*/*")
    public TaskRD getTask(@PathVariable @Min(1) Long id) {
        return service.getById(id);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<TaskRD> search(@PageableDefault(sort = "id") Pageable pageable,
                                   @RequestBody FilterChain filters) {
        return service.search(pageable, filters);
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
