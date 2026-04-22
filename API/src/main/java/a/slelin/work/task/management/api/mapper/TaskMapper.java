package a.slelin.work.task.management.api.mapper;

import a.slelin.work.task.management.api.entity.Status;
import a.slelin.work.task.management.api.entity.Task;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
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
    TaskRD toDto(Task task);

    @Named("takeStatus")
    default String takeStatus(Status status) {
        return status.getDisplayName();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", qualifiedByName = "getStatus")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task patch(@MappingTarget Task task, TaskWD taskDto);
}
