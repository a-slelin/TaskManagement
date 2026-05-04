package a.slelin.work.task.management.core.util.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControllerLoggingUtil {

    public static Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint,
                "\uD83D\uDE80 REST API call",
                "✅ REST API response",
                "❌ REST API");
    }

    public static Object logCall(ProceedingJoinPoint joinPoint) throws Throwable {
        return log(joinPoint,
                "\uD83D\uDD0E PAGE REQUEST",
                "\uD83C\uDD97 PAGE RESPONSE",
                "\uD83D\uDCA5 PAGE RESPONSE");
    }

    private static Object log(ProceedingJoinPoint joinPoint,
                              String info,
                              String success,
                              String error) throws Throwable {

        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
        } catch (IllegalStateException ignore) {
        }

        String httpMethod = request != null ? request.getMethod() : "N/A";
        String requestUri = request != null ? request.getRequestURI() : "N/A";

        log.info("{}: {} {} -> {}.{}(), args: {}", info, httpMethod, requestUri,
                className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("{} completed in \uD83D\uDD5E {} ms", success, elapsed);
            return result;
        } catch (Exception e) {
            log.info("{} error threw: {}", error, e.getMessage());
            throw e;
        }
    }
}
