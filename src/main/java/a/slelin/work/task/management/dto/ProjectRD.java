package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record ProjectRD(@NotNull @Min(1) Long id,
                        String name,
                        String description,
                        List<TaskRD> tasks,
                        @NotNull String user) implements ReadDto {
}
