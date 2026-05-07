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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public TaskRD getTask(@AuthenticationPrincipal Jwt jwt,
                          @PathVariable Long task) {
        return service.getUserTask(extractUserId(jwt), task);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<TaskRD> search(@AuthenticationPrincipal Jwt jwt,
                                   @PageableDefault(sort = "title") Pageable pageable,
                                   @RequestBody FilterChain filters) {
        return service.searchUserTasks(extractUserId(jwt), filters, pageable);
    }

    @PutMapping(path = "/{task}")
    public TaskRD updateTask(@AuthenticationPrincipal Jwt jwt,
                             @PathVariable Long task,
                             @RequestBody TaskWD updTask) {
        return service.updateUserTask(extractUserId(jwt), task, updTask);
    }

    @PatchMapping(path = "/{task}")
    public TaskRD patchTask(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long task,
                            @RequestBody TaskWD pthTask) {
        return service.patchUserTask(extractUserId(jwt), task, pthTask);
    }

    @PatchMapping(path = "/{task}/project/{newProject}", consumes = "*/*")
    public TaskRD setProject(@AuthenticationPrincipal Jwt jwt,
                             @PathVariable Long task,
                             @PathVariable Long newProject) {
        return service.drawToProject(extractUserId(jwt), newProject, task);
    }

    @DeleteMapping(path = "/{task}",
            consumes = "*/*",
            produces = "*/*")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long task) {
        UUID user = extractUserId(jwt);

        service.deleteUserTask(user, task);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
