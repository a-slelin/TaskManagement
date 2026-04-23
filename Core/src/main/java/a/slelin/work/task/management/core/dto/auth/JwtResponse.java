package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record JwtResponse(@NotBlank String accessToken,
                          @NotBlank String refreshToken) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "JwtResponse: accessToken = ***, refreshToken = ***.";
    }
}
