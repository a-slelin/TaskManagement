package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record LoginRequest(@NotBlank String factor,
                           @NotBlank String password) implements WriteDto {

    @NonNull
    @Override
    public String toString() {
        return "LoginRequest: [factor = " + factor + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LoginRequest login = (LoginRequest) o;
        return Objects.equals(factor, login.factor) &&
                Objects.equals(password, login.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factor, password);
    }
}
