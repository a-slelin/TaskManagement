package a.slelin.work.task.management.auth.mapper;

import a.slelin.work.task.management.auth.entity.Gender;
import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.core.dto.auth.UserRD;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    protected PasswordEncoder encoder;

    @Mapping(target = "id", ignore = true)
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
    public abstract UserRD toDto(User user);

    @Named("takeId")
    protected String takeId(UUID id) {
        return id.toString();
    }

    @Named("takeGender")
    protected String takeGender(Gender gender) {
        return gender.getDisplayName();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gender", qualifiedByName = "getGender")
    @Mapping(target = "password", qualifiedByName = "getPassword")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User patch(@MappingTarget User user, UserWD userDto);
}
