package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.dto.UserDto;
import a.slelin.work.task.management.dto.mapper.UserMapper;
import a.slelin.work.task.management.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService implements Service<UUID, UserDto> {

    @Getter
    private final static UserService instance = new UserService();

    private final static UserMapper mapper = Mappers.getMapper(UserMapper.class);

    private final static UserDao repository = UserDao.getInstance();

    @Override
    public List<UserDto> getAll() {
        return repository.getAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserDto getById(UUID id) {
        return mapper.toDto(repository.getById(id));
    }

    @Override
    public UUID create(UserDto dto) {
        User user = mapper.toEntity(dto);
        user = repository.create(user);
        return user.getId();
    }

    @Override
    public UserDto update(UserDto dto) {
        User user = mapper.toEntity(dto);
        user = repository.update(user);
        return mapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
