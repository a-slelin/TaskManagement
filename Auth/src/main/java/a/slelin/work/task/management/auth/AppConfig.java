package a.slelin.work.task.management.auth;

import a.slelin.work.task.management.auth.util.JwtHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(JwtHolder.class)
public class AppConfig {
}
