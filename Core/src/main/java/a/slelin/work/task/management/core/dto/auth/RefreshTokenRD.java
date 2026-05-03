package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RefreshTokenRD(@NotBlank String id,
                             @NotBlank String token,
                             @NotNull LocalDateTime createdAt,
                             @NotNull LocalDateTime expiryDate) implements ReadDto {
}
