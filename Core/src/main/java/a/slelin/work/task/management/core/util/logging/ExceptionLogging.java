package a.slelin.work.task.management.core.util.logging;

import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@SuppressWarnings({"unchecked", "unused"})
public class ExceptionLogging {

    @AfterReturning(
            pointcut = "execution(* a.slelin.work.task.management.core.exception.handler.CustomExceptionHandler.*(..)))",
            returning = "result"
    )
    public void customError(Object result) {
        ErrorResponse errorResponse = ((ResponseEntity<ErrorResponse>) result).getBody();
        ExceptionLoggingUtil.businessError(errorResponse);
    }

    @AfterReturning(
            pointcut = "execution(* a.slelin.work.task.management.core.exception.handler.SecurityExceptionHandler.*(..))",
            returning = "result"
    )
    public void securityError(Object result) {
        ErrorResponse errorResponse = ((ResponseEntity<ErrorResponse>) result).getBody();
        ExceptionLoggingUtil.securityError(errorResponse);
    }

    @AfterReturning(
            pointcut = "execution(* a.slelin.work.task.management.core.exception.handler.GlobalExceptionHandler.*(..))",
            returning = "result"
    )
    public void globalError(Object result) {
        ErrorResponse errorResponse = ((ResponseEntity<ErrorResponse>) result).getBody();
        ExceptionLoggingUtil.globalError(errorResponse);
    }
}
