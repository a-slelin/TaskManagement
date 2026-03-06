package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record UserDto(String id,
                      String username,
                      String password,
                      String gender,
                      String phone,
                      String email) implements Dto {
}
