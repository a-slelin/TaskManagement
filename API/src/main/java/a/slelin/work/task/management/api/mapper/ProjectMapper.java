package a.slelin.work.task.management.api.mapper;

import a.slelin.work.task.management.api.entity.Project;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import org.mapstruct.*;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    Project toEntity(ProjectWD project);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    ProjectRD toDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project patch(@MappingTarget Project project, ProjectWD projectWD);
}
