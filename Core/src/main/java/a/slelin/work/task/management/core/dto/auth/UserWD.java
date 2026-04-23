package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;
import lombok.NonNull;

@Builder
@SuppressWarnings("unused")
public record UserWD(String username,
                     String password,
                     String gender,
                     String phone,
                     String email) implements WriteDto {

    @NonNull
    @Override
    public String toString() {
        return "UserWD: username = " + username
                + ", password = ***, gender = " + gender
                + ", phone = " + phone + ", email = " + email;
    }
}
