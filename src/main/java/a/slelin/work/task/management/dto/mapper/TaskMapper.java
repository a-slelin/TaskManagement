package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.TaskDto;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface TaskMapper {

    @Mapping(target = "status",
            expression = "java(a.slelin.work.task.management.entity.Status.of(task.status()))")
    @Mapping(target = "project", qualifiedByName = "longToProject")
    Task toEntity(TaskDto task);

    @Mapping(target = "status",
            expression = "java(task.getStatus().getDisplayName())")
    @Mapping(target = "project", qualifiedByName = "projectToLong")
    TaskDto toDto(Task task);

    @Named("longToProject")
    default Project longToProject(Long id) {
        return Project.builder().id(id).build();
    }

    @Named("projectToLong")
    default Long projectToLong(Project project) {
        return project.getId();
    }
}
