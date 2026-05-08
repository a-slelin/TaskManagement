package a.slelin.work.task.management.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    public final static String UNIVERSE_DATE_FORMAT;

    public final static String UNIVERSE_TIME_FORMAT;

    public final static String UNIVERSE_DATETIME_FORMAT;

    public final static DateTimeFormatter UNIVERSE_DATE_FORMATTER;

    public final static DateTimeFormatter UNIVERSE_TIME_FORMATTER;

    public final static DateTimeFormatter UNIVERSE_DATETIME_FORMATTER;

    static {
        UNIVERSE_DATE_FORMAT = "dd.MM.yyyy";

        UNIVERSE_TIME_FORMAT = "HH:mm:ss";

        UNIVERSE_DATETIME_FORMAT = "%s %s".formatted(UNIVERSE_DATE_FORMAT, UNIVERSE_TIME_FORMAT);

        UNIVERSE_DATE_FORMATTER = DateTimeFormatter.ofPattern(UNIVERSE_DATE_FORMAT);

        UNIVERSE_TIME_FORMATTER = DateTimeFormatter.ofPattern(UNIVERSE_TIME_FORMAT);

        UNIVERSE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(UNIVERSE_DATETIME_FORMAT);
    }
}
