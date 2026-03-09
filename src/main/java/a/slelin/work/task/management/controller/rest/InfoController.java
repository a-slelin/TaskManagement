package a.slelin.work.task.management.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class InfoController {

    public static final Map<String, Object> info = Map.of(
            "name", "Task Management System",
            "version", "4.0",
            "description", "REST API for managing tasks and projects",
            "timestamp", LocalDateTime.now().toString(),
            "links", Map.of(
                    "users", "/api/users",
                    "projects", "/api/projects",
                    "tasks", "/api/tasks"
            ));

    @GetMapping
    public Map<String, Object> getInfo() {
        return info;
    }

    @GetMapping("/help")
    public Map<String, Object> getHelp() {
        return info;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo2() {
        return info;
    }
}
