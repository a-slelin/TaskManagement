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
@RequiredArgsConstructor
@RequestMapping(value = "/api/tasks",
        produces = {"application/json", "application/xml", "application/yaml"},
        consumes = {"application/json", "application/xml", "application/yaml"})
public class TaskController {

    private final TaskService service;

    @GetMapping(path = "/{task}", consumes = "*/*")
    public TaskRD getTask(@PathVariable Long task,
                          @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.getUserTask(user, task);
    }

    @PostMapping({"/search", "/filter"})
    public SheetDto<TaskRD> search(@RequestBody FilterChain filters,
                                   @PageableDefault(sort = "title") Pageable pageable,
                                   @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.searchUserTasks(user, filters, pageable);
    }

    @PutMapping(path = "/{task}")
    public TaskRD updateTask(@PathVariable Long task,
                             @RequestBody TaskWD updTask,
                             @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.updateUserTask(user, task, updTask);
    }

    @PatchMapping(path = "/{task}")
    public TaskRD patchTask(@PathVariable Long task,
                            @RequestBody TaskWD pthTask,
                            @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.patchUserTask(user, task, pthTask);
    }

    @PatchMapping(path = "/{task}/project/{newProject}", consumes = "*/*")
    public TaskRD setProject(@PathVariable Long task,
                             @PathVariable Long newProject,
                             @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        return service.drawToProject(user, newProject, task);
    }

    @DeleteMapping(path = "/{task}", consumes = "*/*", produces = "*/*")
    public ResponseEntity<Void> deleteTask(@PathVariable Long task,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID user = extractUserId(jwt);
        service.deleteUserTask(user, task);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}
