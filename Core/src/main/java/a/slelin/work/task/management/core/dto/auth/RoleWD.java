package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;

@Builder
public record RoleWD(String name,
                     String description) implements WriteDto {
}
