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
@RequestMapping(value = "/", produces = {"application/json", "application/xml", "application/yaml"})
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

    @GetMapping(path = {"/auth", "/auth/", "/auth/help", "/auth/help/", "/auth/info", "/auth/info/"})
    public Map<String, Object> getAuthInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", applicationHolder.name());
        map.put("version", applicationHolder.version());
        map.put("description", applicationHolder.description());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("links", Map.of(
                "/auth/login", "Login in the system.",
                "/auth/register", "Register in the system.",
                "/auth/refresh", "Update access & refresh token.",
                "/auth/logout", "Logout from the system (current session).",
                "/auth/logout/all", "Logout from the system (all sessions)."
        ));
        return map;
    }

    @GetMapping(path = {"/api", "/api/", "/api/help", "/api/help/", "/api/info", "/api/info/"})
    public Map<String, Object> getApiInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", applicationHolder.name());
        map.put("version", applicationHolder.version());
        map.put("description", applicationHolder.description());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("links", Map.of(
                "/api/user/**", "Management of your account."
        ));
        return map;
    }

    @GetMapping(path = {"/api/admin", "/api/admin/",
            "/api/admin/help", "/api/admin/help/",
            "/api/admin/info", "/api/admin/info/"})
    public Map<String, Object> getApiAdminInfo() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", applicationHolder.name());
        map.put("version", applicationHolder.version());
        map.put("description", applicationHolder.description());
        map.put("timestamp", LocalDateTime.now().toString());
        map.put("links", Map.of(
                "/api/admin/roles/**", "Management of roles.",
                "/api/admin/users/**", "Management of users.",
                "/api/admin/tokens/**", "Management of refresh tokens."
        ));
        return map;
    }
}
