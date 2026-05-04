package a.slelin.work.task.management.core.util.logging;

import a.slelin.work.task.management.core.exception.ErrorResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionLoggingUtil {

    public static void globalError(ErrorResponse response) {
        log.error("⛔ Global error has occurred : {}", response);
    }

    public static void securityError(ErrorResponse response) {
        log.warn("\uD83D\uDEB7 Security error has occurred : {}", response);
    }

    public static void businessError(ErrorResponse response) {
        log.warn("\uD83D\uDCA5 Business error has occurred : {}", response);
    }
}
