package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.entity.Gender;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    protected PasswordEncoder encoder;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    protected ProjectMapper projectMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "password", qualifiedByName = "getPassword")
    @Mapping(target = "gender", qualifiedByName = "getGender")
    public abstract User toEntity(UserWD user);

    @Named("getGender")
    protected Gender getGender(String genderStr) {
        return Gender.of(genderStr);
    }

    @Named("getPassword")
    protected String getPassword(String password) {
        return encoder.encode(password);
    }

    @Mapping(target = "id", qualifiedByName = "takeId")
    @Mapping(target = "gender", qualifiedByName = "takeGender")
    @Mapping(target = "projects", ignore = true)
    public abstract UserRD toDto(User user);

    @Mapping(target = "id", qualifiedByName = "takeId")
    @Mapping(target = "gender", qualifiedByName = "takeGender")
    @Mapping(target = "projects", qualifiedByName = "takeProjects")
    public abstract UserRD toDtoWithProjects(User user);

    @Mapping(target = "id", qualifiedByName = "takeId")
    @Mapping(target = "gender", qualifiedByName = "takeGender")
    @Mapping(target = "projects", qualifiedByName = "takeProjectsWithTasks")
    public abstract UserRD toDtoWithProjectsAndTasks(User user);

    @Named("takeId")
    protected String takeId(UUID id) {
        return id.toString();
    }

    @Named("takeGender")
    protected String takeGender(Gender gender) {
        return gender.getDisplayName();
    }

    @Named("takeProjects")
    protected List<ProjectRD> takeProjects(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }

        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Named("takeProjectsWithTasks")
    protected List<ProjectRD> takeProjectsWithTasks(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }

        return projects.stream()
                .map(projectMapper::toDtoWithTasks)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "gender", qualifiedByName = "getGender")
    @Mapping(target = "password", qualifiedByName = "getPassword")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User patch(@MappingTarget User user, UserWD userDto);
}
