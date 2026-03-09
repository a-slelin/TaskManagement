package a.slelin.work.task.management.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({"a.slelin.work.task.management.service",
        "a.slelin.work.task.management.dto.mapper"})
public class ServiceConfig {
}
