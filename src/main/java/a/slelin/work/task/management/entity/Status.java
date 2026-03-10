package a.slelin.work.task.management.entity;

import a.slelin.work.task.management.exception.EnumParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    BEGIN("begin", "b"),
    END("end", "e"),
    IN_PROGRESS("in_progress", "p"),
    CANCELED("canceled", "c"),
    ON_HOLD("on_hold", "h");

    private final String displayName;

    private final String shortName;

    public static Status of(String key) {
        if (key == null) {
            return null;
        }

        key = key.trim();

        for (Status status : Status.values()) {
            if (key.equalsIgnoreCase(status.name()) ||
                    key.equalsIgnoreCase(status.displayName) ||
                    key.equalsIgnoreCase(status.shortName)) {
                return status;
            }
        }

        throw new EnumParseException(Status.class, key);
    }
}
