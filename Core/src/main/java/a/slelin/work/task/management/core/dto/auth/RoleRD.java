package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record RoleRD(@NonNull Long id,
                     @NotBlank String name,
                     String description) implements ReadDto {
}
