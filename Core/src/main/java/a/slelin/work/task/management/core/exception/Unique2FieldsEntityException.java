package a.slelin.work.task.management.core.exception;

import lombok.Getter;

@Getter
public class Unique2FieldsEntityException extends UniqueFieldEntityException {

    private final String field2;

    private final Object invalidValue2;

    public Unique2FieldsEntityException(Class<?> entity,
                                        String field,
                                        Object invalidValue,
                                        String field2,
                                        Object invalidValue2) {
        super(entity, field, invalidValue, ("The \"%s\" & \"%s\" fields of the \"%s\" entity are unique. " +
                "Violation of the restriction with the values \"%s\" & \"%s\"")
                .formatted(field, field2, entity.getSimpleName(),
                        invalidValue.toString(), invalidValue2.toString()));

        this.field2 = field2;
        this.invalidValue2 = invalidValue2;
    }
}
