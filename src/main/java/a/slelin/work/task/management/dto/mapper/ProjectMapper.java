package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectDto;
import a.slelin.work.task.management.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectMapper {

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user",
            expression = "java(a.slelin.work.task.management.entity.User.byId(project.user()))")
    Project toEntity(ProjectDto project);

    @Mapping(target = "user",
            expression = "java(project.getUser().getId().toString())")
    ProjectDto toDto(Project project);
}
