package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
@SuppressWarnings("unused")
public record UserRD(@NotNull String id,
                     @NotNull String username,
                     String gender,
                     String phone,
                     String email) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "UserRD: [id = %s, username = %s, gender = %s, phone = %s, email = %s]"
                .formatted(id, username, gender, phone, email);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRD user = (UserRD) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, gender, phone, email);
    }
}
