package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record JwtResponse(@NotBlank String accessToken,
                          @NotBlank String refreshToken) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "JwtResponse: [hashCode = %s]".formatted(this.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JwtResponse jwt = (JwtResponse) o;
        return Objects.equals(accessToken, jwt.accessToken) &&
                Objects.equals(refreshToken, jwt.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }
}
