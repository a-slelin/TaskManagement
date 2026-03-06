package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectDto;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface ProjectMapper {

    @Mapping(target = "user", qualifiedByName = "stringToUser")
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectDto project);

    @Mapping(target = "user", qualifiedByName = "userToString")
    ProjectDto toDto(Project project);

    @Named("stringToUser")
    default User stringToUser(String user) {
        return User.builder()
                .id(UUID.fromString(user))
                .build();
    }

    @Named("userToString")
    default String userToString(User user) {
        return user.getId().toString();
    }
}
