package a.slelin.work.task.management.auth.controller.rest;

import a.slelin.work.task.management.auth.util.ApplicationHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/",
        produces = {"application/json", "application/xml", "application/yaml"})
public class InfoController {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private ApplicationHolder applicationHolder;

    @GetMapping(path = {"/help", "/help/", "/info", "/info/"})
    public Map<String, Object> getInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", applicationHolder.name());
        map.put("version", applicationHolder.version());
        map.put("description", applicationHolder.description());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("links", Map.of(
                "/auth/**", "Authentication options.",
                "/api/**", "Management of users, roles and tokens."
        ));
        return map;
    }
}
