package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Status;
import a.slelin.work.task.management.entity.Task;
import org.mapstruct.*;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", qualifiedByName = "getStatus")
    Task toEntity(TaskWD task);

    @Named("getStatus")
    default Status getStatus(String statusStr) {
        return Status.of(statusStr);
    }

    @Mapping(target = "status", qualifiedByName = "takeStatus")
    @Mapping(target = "project", qualifiedByName = "takeProject")
    @Mapping(target = "user", source = ".", qualifiedByName = "takeUser")
    TaskRD toDto(Task task);

    @Named("takeStatus")
    default String takeStatus(Status status) {
        return status.getDisplayName();
    }

    @Named("takeProject")
    default Long takeProject(Project project) {
        return project.getId();
    }

    @Named("takeUser")
    default String takeUser(Task task) {
        return task.getProject().getUser().getId().toString();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", qualifiedByName = "getStatus")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task patch(@MappingTarget Task task, TaskWD taskDto);
}
