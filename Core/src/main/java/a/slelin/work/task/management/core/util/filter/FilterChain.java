package a.slelin.work.task.management.core.util.filter;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public record FilterChain(@NotNull List<@NotNull Filter> filters) {

    public boolean isEmpty() {
        return filters.isEmpty();
    }

    public void clear() {
        filters.clear();
    }

    public int size() {
        return filters.size();
    }

    public static FilterChain empty() {
        return new FilterChain(new ArrayList<>());
    }

    public static FilterChain of(@NotNull Filter... filters) {
        return new FilterChain(Arrays.stream(filters)
                .filter(Objects::nonNull)
                .toList());
    }

    public static FilterChain of(@NotNull List<Filter> filters) {
        return new FilterChain(new ArrayList<>(filters).stream()
                .filter(Objects::nonNull)
                .toList());
    }

    public FilterChain copy() {
        return new FilterChain(new ArrayList<>(filters));
    }

    public FilterChain add(@NotNull Filter filter) {
        if (!filters.contains(filter)) {
            filters.add(filter);
        }

        return this;
    }

    public FilterChain remove(@NotNull Filter filter) {
        filters.remove(filter);
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterChain: [hashCode = %d]".formatted(this.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FilterChain that = (FilterChain) o;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filters);
    }
}
