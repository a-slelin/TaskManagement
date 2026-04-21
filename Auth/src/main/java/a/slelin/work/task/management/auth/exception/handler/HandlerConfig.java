package a.slelin.work.task.management.auth.exception.handler;

import a.slelin.work.task.management.core.exception.handler.BusinessExceptionHandler;
import a.slelin.work.task.management.core.exception.handler.GlobalExceptionHandler;
import a.slelin.work.task.management.core.exception.handler.ServiceExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({GlobalExceptionHandler.class, BusinessExceptionHandler.class, ServiceExceptionHandler.class})
public class HandlerConfig {
}
