package a.slelin.work.task.management.api.controller.rest;

import a.slelin.work.task.management.api.util.ApplicationHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/", produces = {"application/json", "application/xml", "application/yaml"})
public class InfoController {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private ApplicationHolder applicationHolder;

    @GetMapping({"/help", "/help/", "/info", "/info/",
            "/api", "/api/", "/api/help", "/api/help/",
            "/api/info", "/api/info/"})
    public Map<String, Object> getInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", applicationHolder.name());
        map.put("version", applicationHolder.version());
        map.put("description", applicationHolder.description());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("links", Map.of(
                "/api/projects/**", "Management of your projects.",
                "/api/tasks/**", "Management of your tasks."
        ));
        return map;
    }
}
