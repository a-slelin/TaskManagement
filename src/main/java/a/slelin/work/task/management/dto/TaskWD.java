package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record TaskWD(String title,
                     String description,
                     String status) implements WriteDto {
}
