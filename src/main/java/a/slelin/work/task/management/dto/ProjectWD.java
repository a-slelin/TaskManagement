package a.slelin.work.task.management.dto;

import lombok.Builder;

@Builder
public record ProjectWD(String name,
                        String description,
                        String user) implements WriteDto {

    public static ProjectWD.ProjectWDBuilder intercept(ProjectWD project) {
        return ProjectWD.builder()
                .name(project.name())
                .description(project.description())
                .user(project.user());
    }
}
