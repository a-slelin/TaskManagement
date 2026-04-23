package a.slelin.work.task.management.core.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundByPropertyException extends RuntimeException {

    private final Class<?> entity;

    private final String property;

    private final Object invalidProperty;

    @SuppressWarnings("unused")
    public EntityNotFoundByPropertyException(Class<?> entity,
                                             String property,
                                             Object invalidProperty) {
        this(entity, property, invalidProperty, "Entity '%s' not found by property '%s' = '%s'."
                .formatted(entity.getSimpleName(),
                        property,
                        invalidProperty.toString()));
    }

    public EntityNotFoundByPropertyException(Class<?> entity,
                                             String property,
                                             Object invalidProperty,
                                             String message) {
        this.entity = entity;
        this.property = property;
        this.invalidProperty = invalidProperty;
        super(message);
    }
}
