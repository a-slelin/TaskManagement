package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record LoginRequest(@NotBlank String factor,
                           @NotBlank String password) implements WriteDto {

    @NonNull
    @Override
    public String toString() {
        return "LoginRequest: factor = " + factor + ", password = ***.";
    }
}
