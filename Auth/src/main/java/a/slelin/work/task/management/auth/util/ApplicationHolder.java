package a.slelin.work.task.management.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record ApplicationHolder(@Value("${spring.application.name}") String name,
                                @Value("${spring.application.version}") String version,
                                @Value("Auth Server for Task Management System Web App") String description) {
}
