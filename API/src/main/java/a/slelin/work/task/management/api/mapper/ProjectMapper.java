package a.slelin.work.task.management.api.mapper;

import a.slelin.work.task.management.api.entity.Project;
import a.slelin.work.task.management.api.entity.Task;
import a.slelin.work.task.management.api.entity.User;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public abstract class ProjectMapper {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    protected TaskMapper taskMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract Project toEntity(ProjectWD project);

    @Mapping(target = "user", qualifiedByName = "takeUser")
    @Mapping(target = "tasks", qualifiedByName = "takeTasks")
    public abstract ProjectRD toDtoWithTasks(Project project);

    @Mapping(target = "user", qualifiedByName = "takeUser")
    @Mapping(target = "tasks", ignore = true)
    public abstract ProjectRD toDto(Project project);

    @Named("takeUser")
    protected String takeUser(User user) {
        return user.getId().toString();
    }

    @Named("takeTasks")
    protected List<TaskRD> takeTasks(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }

        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Project patch(@MappingTarget Project project, ProjectWD projectWD);
}
