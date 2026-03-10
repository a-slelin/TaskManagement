package a.slelin.work.task.management.util.logging;

import a.slelin.work.task.management.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@SuppressWarnings("unchecked")
public class ExceptionLogging {

    @After("execution(* a.slelin.work.task.management.exception.handler.BusinessExceptionHandler.*(..)))")
    public void after(JoinPoint joinPoint) {
        log.info("\uD83E\uDDF0 Business error has occurred : {}", ((Exception) joinPoint.getArgs()[0]).getMessage());
    }

    @AfterReturning(value = "execution(* a.slelin.work.task.management.exception.handler.ServiceExceptionHandler.*(..)))",
            returning = "result")
    public void after(Object result) {
        log.warn("⚠️ Service error has occurred : {}", ((ResponseEntity<ErrorResponse>) result).getBody());
    }

    @AfterReturning(pointcut = "execution(* a.slelin.work.task.management.exception.handler.GlobalExceptionHandler.*(..))",
            returning = "result")
    public void after2(Object result) {
        log.error("⛔ Global error has occurred : {}", ((ResponseEntity<ErrorResponse>) result).getBody());
    }
}
