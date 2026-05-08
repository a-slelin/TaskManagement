package a.slelin.work.task.management.core.exception;

import lombok.Getter;

@Getter
public class UniqueFieldEntityException extends RuntimeException {

    private final Class<?> entity;

    private final String field;

    private final Object invalidValue;

    public UniqueFieldEntityException(Class<?> entity, String field, Object invalidValue) {
        this(entity, field, invalidValue,
                ("The \"%s\" field of the \"%s\" entity is unique. " +
                        "Violation of the restriction with the value \"%s\"")
                        .formatted(field, entity.getSimpleName(), invalidValue.toString()));
    }

    public UniqueFieldEntityException(Class<?> entity, String field, Object invalidValue, String message) {
        super(message);

        this.entity = entity;
        this.field = field;
        this.invalidValue = invalidValue;
    }
}
