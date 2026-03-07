package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record TaskWD(String title,
                     String description,
                     String status,
                     @Min(1) Long project) implements WriteDto {
}
