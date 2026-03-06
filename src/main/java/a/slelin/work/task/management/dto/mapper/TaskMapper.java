package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.TaskDto;
import a.slelin.work.task.management.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {

    @Mapping(target = "status",
            expression = "java(a.slelin.work.task.management.entity.Status.of(task.status()))")
    @Mapping(target = "project",
            expression = "java(a.slelin.work.task.management.entity.Project.byId(task.project()))")
    Task toEntity(TaskDto task);

    @Mapping(target = "status",
            expression = "java(task.getStatus().getDisplayName())")
    @Mapping(target = "project",
            expression = "java(task.getProject().getId())")
    TaskDto toDto(Task task);
}
