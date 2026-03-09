package a.slelin.work.task.management.exception;

import lombok.Getter;

@Getter
public class EnumParseException extends RuntimeException {

    private final Class<? extends Enum<?>> enumClass;

    private final Object invalidKey;

    public EnumParseException(Class<? extends Enum<?>> enumClass, Object invalidKey) {
        this(enumClass, invalidKey, "Cannot parse enum '%s' from key '%s'."
                .formatted(enumClass.getSimpleName(), invalidKey.toString()));
    }

    public EnumParseException(Class<? extends Enum<?>> enumClass, Object invalidKey, String message) {
        this.enumClass = enumClass;
        this.invalidKey = invalidKey;
        super(message);
    }
}
