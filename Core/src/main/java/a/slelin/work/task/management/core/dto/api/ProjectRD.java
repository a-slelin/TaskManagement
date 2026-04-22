package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProjectRD(@NotNull @Min(1) Long id,
                        String name,
                        String description) implements ReadDto {
}
