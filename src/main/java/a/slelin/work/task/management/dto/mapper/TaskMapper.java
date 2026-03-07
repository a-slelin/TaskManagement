package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Status;
import a.slelin.work.task.management.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
@SuppressWarnings("unused")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", qualifiedByName = "getStatus")
    @Mapping(target = "project", qualifiedByName = "getProject")
    Task toEntity(TaskWD task);

    @Named("getStatus")
    default Status getStatus(String statusStr) {
        return Status.of(statusStr);
    }

    @Named("getProject")
    default Project getProject(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return ProjectDao.getInstance().findById(projectId);
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
}
