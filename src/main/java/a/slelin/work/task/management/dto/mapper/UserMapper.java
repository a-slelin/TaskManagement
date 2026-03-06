package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.UserDto;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "id",
            expression = "java(java.util.UUID.fromString(user.id()))")
    @Mapping(target = "gender",
            expression = "java(a.slelin.work.task.management.entity.Gender.of(user.gender()))")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "projects", ignore = true)
    User toEntity(UserDto user);

    @Mapping(target = "id",
            expression = "java(user.getId().toString())")
    @Mapping(target = "gender",
            expression = "java(user.getGender().getDisplayName())")
    UserDto toDto(User user);
}
