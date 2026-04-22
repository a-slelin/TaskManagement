package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;

@Builder
@SuppressWarnings("unused")
public record UserWD(String username,
                     String password,
                     String gender,
                     String phone,
                     String email) implements WriteDto {
}
