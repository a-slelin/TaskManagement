package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.util.List;

@Builder
public record SortDto(@NotNull @Pattern(regexp = "^[A-Za-z.]+$") String property,
                      @NotNull @Pattern(regexp = "^(?i)asc|desc$") String direction) implements ReadDto {

    public static List<SortDto> of(Sort sort) {
        if (sort == null) {
            throw new IllegalArgumentException("Sort must not be null.");
        }

        return sort.stream()
                .map(order -> SortDto.builder()
                        .property(order.getProperty())
                        .direction(order.getDirection().toString().toLowerCase())
                        .build())
                .toList();
    }
}
