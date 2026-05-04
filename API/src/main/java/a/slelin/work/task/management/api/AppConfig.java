package a.slelin.work.task.management.api;

import a.slelin.work.task.management.core.exception.handler.BaseHandlers;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseHandlers.class)
public class AppConfig {
}
