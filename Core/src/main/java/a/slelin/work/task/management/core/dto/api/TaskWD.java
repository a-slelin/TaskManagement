package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;

@Builder
@SuppressWarnings("unused")
public record TaskWD(String title,
                     String description,
                     String status) implements WriteDto {
}
