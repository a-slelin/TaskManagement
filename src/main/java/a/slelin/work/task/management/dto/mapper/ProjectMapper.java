package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@SuppressWarnings("unused")
@Mapper(componentModel = "cdi")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    Project toEntity(ProjectWD project);

    @Mapping(target = "user", qualifiedByName = "takeUser")
    @Mapping(target = "tasks", qualifiedByName = "takeTasks")
    ProjectRD toDto(Project project);

    @Named("takeUser")
    default String takeUser(User user) {
        return user.getId().toString();
    }

    @Named("takeTasks")
    default List<TaskRD> takeTasks(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }

        return tasks.stream()
                .map(Mappers.getMapper(TaskMapper.class)::toDto)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project patch(@MappingTarget Project project, ProjectWD projectWD);
}
