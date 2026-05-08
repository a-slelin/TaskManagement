package a.slelin.work.task.management.api;

import a.slelin.work.task.management.core.exception.handler.BaseHandlers;
import a.slelin.work.task.management.core.util.logging.ExceptionLogging;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({BaseHandlers.class, ExceptionLogging.class})
public class AppConfig {
}
