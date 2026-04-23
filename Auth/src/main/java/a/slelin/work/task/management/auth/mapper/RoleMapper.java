package a.slelin.work.task.management.auth.mapper;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.core.dto.auth.RoleRD;
import a.slelin.work.task.management.core.dto.auth.RoleWD;
import org.mapstruct.*;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleWD dto);

    RoleRD toDTO(Role entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role patch(@MappingTarget Role entity, RoleWD dto);
}
