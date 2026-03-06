package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record ProjectDto(Long id,
                         String name,
                         String description) implements Dto {
}
