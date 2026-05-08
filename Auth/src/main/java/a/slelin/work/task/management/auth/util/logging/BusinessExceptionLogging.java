package a.slelin.work.task.management.auth.util.logging;

import a.slelin.work.task.management.core.exception.ErrorResponse;
import a.slelin.work.task.management.core.util.logging.ExceptionLoggingUtil;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@SuppressWarnings({"unchecked", "unused"})
public class BusinessExceptionLogging {

    @AfterReturning(
            pointcut = "execution(* a.slelin.work.task.management.auth.exception.handler.BusinessExceptionHandler.*(..)))",
            returning = "result"
    )
    public void businessError(Object result) {
        ErrorResponse errorResponse = ((ResponseEntity<ErrorResponse>) result).getBody();
        ExceptionLoggingUtil.businessError(errorResponse);
    }
}
