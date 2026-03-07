package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TaskRD(@NotNull @Min(1) Long id,
                     String title,
                     String description,
                     String status,
                     @NotNull @Min(1) Long project,
                     @NotNull String user) implements ReadDto {
}
