package a.slelin.work.task.management.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundByIdException extends RuntimeException {

    private final Class<?> entity;

    private final Object invalidKey;

    public EntityNotFoundByIdException(Class<?> entity, Object invalidKey) {
        this(entity, invalidKey, "Entity '%s' not found by id = '%s'."
                .formatted(entity.getSimpleName(), invalidKey.toString()));
    }

    public EntityNotFoundByIdException(Class<?> entity, Object invalidKey, String message) {
        this.entity = entity;
        this.invalidKey = invalidKey;
        super(message);
    }
}
