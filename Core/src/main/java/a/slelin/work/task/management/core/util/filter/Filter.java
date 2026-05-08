package a.slelin.work.task.management.core.util.filter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record Filter(@NotNull @Pattern(regexp = "^[A-Za-z.]+$") String field,
                     @NotNull Operation operation,
                     Object value,
                     Object value2) {

    @SuppressWarnings("unused")
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

    public Filter not() {
        return not(this);
    }

    @NonNull
    @Override
    public String toString() {
        String result = "Filter: [field = %s, operation = %s"
                .formatted(field, operation.getDisplayName());

        if (value != null) {
            result += ", value = %s".formatted(value);
        }

        if (value2 != null) {
            result += ", value2 = %s".formatted(value2);
        }

        return result + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Filter filter = (Filter) o;
        return Objects.equals(field, filter.field) &&
                Objects.equals(value, filter.value) &&
                Objects.equals(value2, filter.value2) &&
                operation == filter.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, operation, value, value2);
    }
}
