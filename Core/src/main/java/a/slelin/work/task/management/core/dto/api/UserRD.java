package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
@SuppressWarnings("unused")
public record UserRD(@NotNull String id,
                     @NotNull String username,
                     String gender,
                     String phone,
                     String email,
                     List<ProjectRD> projects) implements ReadDto {
}
