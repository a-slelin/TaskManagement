package a.slelin.work.task.management.core.exception;

public class EntityNotFoundByIdException extends EntityNotFoundByPropertyException {

    @SuppressWarnings("unused")
    public EntityNotFoundByIdException(Class<?> entity, Object invalidKey) {
        this(entity, invalidKey, "Entity '%s' not found by id = '%s'."
                .formatted(entity.getSimpleName(), invalidKey.toString()));
    }

    public EntityNotFoundByIdException(Class<?> entity, Object invalidKey, String message) {
        super(entity, "id", invalidKey, message);
    }
}
