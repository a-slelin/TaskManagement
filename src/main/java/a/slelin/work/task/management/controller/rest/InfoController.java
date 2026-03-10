package a.slelin.work.task.management.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(value = "/api", produces = {"application/json", "application/xml", "application/yaml"})
public class InfoController {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.application.version}")
    private String version;

    @GetMapping({"", "/", "/help", "/help/", "/info", "/info/"})
    public Map<String, Object> getInfo() {
        return Map.of(
                "name", name,
                "version", version,
                "description", "REST API for managing tasks and projects",
                "timestamp", LocalDateTime.now().toString(),
                "links", Map.of(
                        "users", "/api/users",
                        "projects", "/api/projects",
                        "tasks", "/api/tasks"
                ));
    }
}
