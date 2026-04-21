package a.slelin.work.task.management.api.dto;

import lombok.Builder;

@Builder
public record UserWD(String username,
                     String password,
                     String gender,
                     String phone,
                     String email) implements WriteDto {
}
