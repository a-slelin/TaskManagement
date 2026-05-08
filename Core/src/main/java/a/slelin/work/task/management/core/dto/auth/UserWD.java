package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
@SuppressWarnings("unused")
public record UserWD(String username,
                     String password,
                     String gender,
                     String phone,
                     String email) implements WriteDto {

    public UserWD(String username, String password) {
        this(username, password, null);
    }

    public UserWD(String username, String password, String gender) {
        this(username, password, gender, null, null);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserWD: [username = %s, gender = %s, phone = %s, email = %s]"
                .formatted(username, gender, phone, email);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserWD user = (UserWD) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, gender, phone, email);
    }
}
