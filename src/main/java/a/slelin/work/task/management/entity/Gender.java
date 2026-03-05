package a.slelin.work.task.management.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("male", "m"),
    FEMALE("female", "f"),
    UNDEFINED("undefined", "und");

    private final String displayName;

    private final String shortName;
}
