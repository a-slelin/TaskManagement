package a.slelin.work.task.management.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record ApplicationHolder(@Value("${spring.application.name}") String name,
                                @Value("${spring.application.version}") String version,
                                @Value("Resource API Server for Task Management System Web App") String description) {
}
