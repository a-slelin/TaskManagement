package a.slelin.work.task.management.api.dto;

import lombok.Builder;

@Builder
public record TaskWD(String title,
                     String description,
                     String status) implements WriteDto {
}
