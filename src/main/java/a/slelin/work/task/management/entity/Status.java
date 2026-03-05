package a.slelin.work.task.management.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    BEGIN("begin", "b"),
    END("end", "e"),
    IN_PROGRESS("in_progress", "p"),
    CANCELLED("cancelled", "c"),
    ON_HOLD("on_hold", "h");

    private final String displayName;

    private final String shortName;
}
