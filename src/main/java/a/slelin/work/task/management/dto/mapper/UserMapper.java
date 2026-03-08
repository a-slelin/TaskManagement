package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.entity.Gender;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Mapper(componentModel = "cdi")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "gender", qualifiedByName = "getGender")
    User toEntity(UserWD user);

    @Named("getGender")
    default Gender getGender(String genderStr) {
        return Gender.of(genderStr);
    }

    @Mapping(target = "id", qualifiedByName = "takeId")
    @Mapping(target = "gender", qualifiedByName = "takeGender")
    @Mapping(target = "projects", qualifiedByName = "takeProjects")
    UserRD toDto(User user);

    @Named("takeId")
    default String takeId(UUID id) {
        return id.toString();
    }

    @Named("takeGender")
    default String takeGender(Gender gender) {
        return gender.getDisplayName();
    }

    @Named("takeProjects")
    default List<ProjectRD> takeProjects(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }

        return projects.stream()
                .map(Mappers.getMapper(ProjectMapper.class)::toDto)
                .toList();
    }
}
