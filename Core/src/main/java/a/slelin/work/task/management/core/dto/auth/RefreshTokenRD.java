package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Objects;

import static a.slelin.work.task.management.core.util.DateTimeUtil.UNIVERSE_DATETIME_FORMATTER;

@Builder
public record RefreshTokenRD(@NotBlank String id,
                             @NotBlank String token,
                             @NotNull LocalDateTime createdAt,
                             @NotNull LocalDateTime expiryDate) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "RefreshTokenRD: [id = %s, createdAt = %s, expiryDate = %s]"
                .formatted(id, createdAt.format(UNIVERSE_DATETIME_FORMATTER),
                        expiryDate.format(UNIVERSE_DATETIME_FORMATTER));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RefreshTokenRD token = (RefreshTokenRD) o;
        return Objects.equals(id, token.id) &&
                Objects.equals(this.token, token.token) &&
                Objects.equals(createdAt, token.createdAt) &&
                Objects.equals(expiryDate, token.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, createdAt, expiryDate);
    }
}
