package a.slelin.work.task.management.api.util.logging;

import a.slelin.work.task.management.core.util.logging.ControllerLoggingUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@SuppressWarnings("unused")
public class ControllerLogging {

    @Around("execution(* a.slelin.work.task.management.api.controller.rest..*.*(..))")
    public Object logRest(ProceedingJoinPoint joinPoint) throws Throwable {
        return ControllerLoggingUtil.logRestCall(joinPoint);
    }

    @Around("execution(* a.slelin.work.task.management.api.controller..*.*(..)) && " +
            "!execution(* a.slelin.work.task.management.api.controller.rest..*.*(..)) && " +
            "!@annotation(org.springframework.web.bind.annotation.ModelAttribute)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return ControllerLoggingUtil.logCall(joinPoint);
    }
}
