package a.slelin.work.task.management.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.Map;

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
}

