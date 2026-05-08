package a.slelin.work.task.management.core.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Builder
public record PageDto(@NotNull @Min(0) Integer number,
                      @NotNull @Min(1) Integer size,
                      @NotNull @Valid List<SortDto> sorts,
                      @NotNull @Min(0) Long totalElements,
                      @NotNull @Min(0) Integer totalPages,
                      boolean first,
                      boolean last,
                      boolean empty) implements ReadDto {

    public static PageDto of(Page<?> page) {
        if (page == null) {
            throw new IllegalArgumentException("Page should be not null.");
        }

        return PageDto.builder()
                .number(page.getNumber())
                .size(page.getSize())
                .sorts(SortDto.of(page.getSort()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    @NonNull
    @Override
    public String toString() {
        return "PageDto: [number = %d, size = %d, totalElements = %d, totalPages = %d]"
                .formatted(number, size, totalElements, totalPages);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PageDto page = (PageDto) o;
        return last == page.last &&
                first == page.first &&
                empty == page.empty &&
                Objects.equals(size, page.size) &&
                Objects.equals(number, page.number) &&
                Objects.equals(totalElements, page.totalElements) &&
                Objects.equals(totalPages, page.totalPages) &&
                Objects.equals(sorts, page.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, size, sorts, totalElements, totalPages, first, last, empty);
    }
}
