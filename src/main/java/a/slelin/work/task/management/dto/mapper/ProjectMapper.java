package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectDto;
import a.slelin.work.task.management.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectDto project);

    ProjectDto toDto(Project project);
}
