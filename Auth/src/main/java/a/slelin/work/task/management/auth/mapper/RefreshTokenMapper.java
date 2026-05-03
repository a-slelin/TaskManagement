package a.slelin.work.task.management.auth.mapper;

import a.slelin.work.task.management.auth.entity.RefreshToken;
import a.slelin.work.task.management.core.dto.auth.RefreshTokenRD;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "id", qualifiedByName = "takeId")
    RefreshTokenRD toDTO(RefreshToken entity);

    @Named("takeId")
    default String takeId(UUID id) {
        return id.toString();
    }
}
