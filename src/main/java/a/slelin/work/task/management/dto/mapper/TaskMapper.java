package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.TaskDto;
import a.slelin.work.task.management.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {

    @Mapping(target = "status",
            expression = "java(a.slelin.work.task.management.entity.Status.of(task.status()))")
    @Mapping(target = "project", ignore = true)
    Task toEntity(TaskDto task);

    @Mapping(target = "status",
            expression = "java(task.getStatus().getDisplayName())")
    TaskDto toDto(Task task);
}
