package a.slelin.work.task.management.api.dto;

import lombok.Builder;

@Builder
public record ProjectWD(String name,
                        String description) implements WriteDto {
}
