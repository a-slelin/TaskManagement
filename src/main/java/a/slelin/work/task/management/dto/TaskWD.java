package a.slelin.work.task.management.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record TaskWD(String title,
                     String description,
                     String status,
                     @Min(1) Long project) implements WriteDto {

    public static TaskWD.TaskWDBuilder intercept(TaskWD task) {
        return TaskWD.builder()
                .title(task.title())
                .description(task.description())
                .status(task.status())
                .project(task.project());
    }
}
