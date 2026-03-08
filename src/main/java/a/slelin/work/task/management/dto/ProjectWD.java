package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record ProjectWD(String name,
                        String description) implements WriteDto {
}
