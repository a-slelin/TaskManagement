package a.slelin.work.task.management.dto.mapper;

import a.slelin.work.task.management.dto.UserDto;
import a.slelin.work.task.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "id",
            expression = "java(user.id() == null ? null : java.util.UUID.fromString(user.id()))")
    @Mapping(target = "gender",
            expression = "java(a.slelin.work.task.management.entity.Gender.of(user.gender()))")
    @Mapping(target = "projects", ignore = true)
    User toEntity(UserDto user);

    @Mapping(target = "id",
            expression = "java(user.getId().toString())")
    @Mapping(target = "gender",
            expression = "java(user.getGender() == null ? null : user.getGender().getDisplayName())")
    @Mapping(target = "password", expression = "java(java.util.UUID.randomUUID().toString())")
    UserDto toDto(User user);
}
