package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UserRD(@NotNull String id,
                     @NotNull String username,
                     String gender,
                     String phone,
                     String email,
                     List<ProjectRD> projects) implements ReadDto {
}
