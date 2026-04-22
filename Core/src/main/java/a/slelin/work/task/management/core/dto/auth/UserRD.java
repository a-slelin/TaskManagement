package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@SuppressWarnings("unused")
public record UserRD(@NotNull String id,
                     @NotNull String username,
                     String gender,
                     String phone,
                     String email) implements ReadDto {
}
