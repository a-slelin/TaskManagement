package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record TaskDto(Long id,
                      String title,
                      String description,
                      String status,
                      Long project) implements Dto {
}
