package a.slelin.work.task.management.util.filter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record Filter(@NotNull @Pattern(regexp = "^[A-Za-z.]+$") String field,
                     @NotNull Operation operation,
                     Object value,
                     Object value2) {

    public static Filter of(String field, Operation operation) {
        return of(field, operation, null);
    }

    public static Filter of(String field, Operation operation, Object value) {
        return of(field, operation, value, null);
    }

    public static Filter of(String field, Operation operation, Object value, Object value2) {
        return Filter.builder()
                .field(field)
                .operation(operation)
                .value(value)
                .value2(value2)
                .build();
    }

    public static Filter not(Filter filter) {
        return Filter.builder()
                .field(filter.field)
                .operation(filter.operation.getOpposite())
                .value(filter.value)
                .value2(filter.value2)
                .build();
    }
}
