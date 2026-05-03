package a.slelin.work.task.management.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    public final static String UNIVERSE_DATETIME_FORMAT;

    public final static DateTimeFormatter UNIVERSE_DATETIME_FORMATTER;

    static {
        UNIVERSE_DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

        UNIVERSE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(UNIVERSE_DATETIME_FORMAT);
    }
}
