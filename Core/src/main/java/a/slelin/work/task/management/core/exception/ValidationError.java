package a.slelin.work.task.management.core.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
public record ValidationError(@NotBlank String field,
                              String message,
                              @NotNull Object value,
                              String type,
                              String path,
                              Map<String, Object> details) {

    public static List<ValidationError> fromException(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(ValidationError::fromConstraintViolation)
                .toList();
    }

    public static ValidationError fromConstraintViolation(ConstraintViolation<?> violation) {
        var descriptor = violation.getConstraintDescriptor();

        return ValidationError.builder()
                .field(getFieldName(violation.getPropertyPath()))
                .message(violation.getMessage())
                .value(violation.getInvalidValue())
                .type(descriptor.getAnnotation().annotationType().getSimpleName())
                .path(violation.getPropertyPath().toString())
                .details(descriptor.getAttributes())
                .build();
    }

    private static String getFieldName(Path path) {
        String fullPath = path.toString();
        return fullPath.contains(".") ?
                fullPath.substring(fullPath.lastIndexOf('.') + 1) : fullPath;
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationError: [field = %s, message = %s, value = %s, type = %s, path = %s, details = %s]"
                .formatted(field, message, value, type, path, details);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ValidationError that = (ValidationError) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(path, that.path) &&
                Objects.equals(field, that.field) &&
                Objects.equals(value, that.value) &&
                Objects.equals(message, that.message) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message, value, type, path, details);
    }
}

