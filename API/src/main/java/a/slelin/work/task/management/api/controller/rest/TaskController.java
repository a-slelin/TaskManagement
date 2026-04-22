package a.slelin.work.task.management.api.controller.rest;

import a.slelin.work.task.management.api.service.TaskService;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/tasks",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping(path = "/{task}", consumes = "*/*")
    public TaskRD getTask(@PathVariable Long task) {
        UUID user = getUserFromSecurityContext();
        return service.getUserTask(user, task);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<TaskRD> search(@PageableDefault(sort = "title") Pageable pageable,
                                   @RequestBody FilterChain filters) {
        UUID user = getUserFromSecurityContext();
        return service.searchUserTasks(user, filters, pageable);
    }

    @PutMapping(path = "/{task}")
    public TaskRD updateTask(@PathVariable Long task,
                             @RequestBody TaskWD updTask) {
        UUID user = getUserFromSecurityContext();
        return service.updateUserTask(user, task, updTask);
    }

    @PatchMapping(path = "/{task}")
    public TaskRD patchTask(@PathVariable Long task,
                            @RequestBody TaskWD pthTask) {
        UUID user = getUserFromSecurityContext();
        return service.patchUserTask(user, task, pthTask);
    }

    @PostMapping(path = "/{task}/project/{newProject}", consumes = "*/*")
    public TaskRD setProject(@PathVariable Long task,
                             @PathVariable Long newProject) {
        UUID user = getUserFromSecurityContext();
        return service.drawToProject(user, task, newProject);
    }

    @DeleteMapping(path = "/{task}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteTask(@PathVariable Long task) {
        UUID user = getUserFromSecurityContext();

        service.deleteUserTask(user, task);
        return ResponseEntity.noContent().build();
    }

    private UUID getUserFromSecurityContext() {
        return UUID.randomUUID();
    }
}
