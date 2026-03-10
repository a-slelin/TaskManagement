package a.slelin.work.task.management.util.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerLogging {

    @Around("execution(* a.slelin.work.task.management.controller.rest..*.*(..))")
    public Object logRest(ProceedingJoinPoint joinPoint) throws Throwable {

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

        log.info("\uD83D\uDE80 REST API call: {} {} -> {}.{}(), args: {}", httpMethod, requestUri,
                className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("✅ REST API response completed in \uD83D\uDD5E {} ms", elapsed);
            return result;
        } catch (Exception e) {
            log.info("❌ REST API error threw: {}", e.getMessage());
            throw e;
        }
    }

    @Around("execution(* a.slelin.work.task.management.controller..*.*(..)) && " +
            "!execution(* a.slelin.work.task.management.controller.rest..*.*(..)) && " +
            "!@annotation(org.springframework.web.bind.annotation.ModelAttribute)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

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

        log.info("\uD83D\uDD0E PAGE REQUEST: {} {} -> {}.{}(), args: {}", httpMethod, requestUri,
                className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("\uD83C\uDD97 PAGE RESPONSE completed in \uD83D\uDD5E {} ms", elapsed);
            return result;
        } catch (Exception e) {
            log.info("\uD83D\uDCA5 PAGE RESPONSE error threw: {}", e.getMessage());
            throw e;
        }
    }
}
