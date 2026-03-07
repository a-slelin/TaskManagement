package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
@SuppressWarnings("unused")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "user", qualifiedByName = "getUser")
    Project toEntity(ProjectWD project);

    @Named("getUser")
    default User getUser(String userStr) {
        return UserDao.getInstance().getUserById(userStr);
    }

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
}
