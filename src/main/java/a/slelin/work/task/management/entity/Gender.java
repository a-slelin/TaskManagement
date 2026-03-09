package a.slelin.work.task.management.entity;

import a.slelin.work.task.management.exception.EnumParseException;
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

    public static Gender of(String key) {
        if (key == null) {
            return null;
        }

        for (Gender value : Gender.values()) {
            if (key.equalsIgnoreCase(value.name()) ||
                    key.equalsIgnoreCase(value.displayName) ||
                    key.equalsIgnoreCase(value.shortName)) {
                return value;
            }
        }

        throw new EnumParseException(Gender.class, key);
    }
}
