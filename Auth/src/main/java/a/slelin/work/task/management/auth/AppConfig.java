package a.slelin.work.task.management.auth;

import a.slelin.work.task.management.auth.util.JwtHolder;
import a.slelin.work.task.management.core.exception.handler.BaseHandlers;
import a.slelin.work.task.management.core.util.logging.ExceptionLogging;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Import({BaseHandlers.class, ExceptionLogging.class})
@EnableConfigurationProperties(JwtHolder.class)
public class AppConfig {
}
