package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.dto.mapper.UserMapper;
import a.slelin.work.task.management.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService implements Service<UUID, UserRD, UserWD> {

    @Getter
    private final static UserService instance = new UserService();

    private final static UserMapper mapper = Mappers.getMapper(UserMapper.class);

    private final static UserDao repository = UserDao.getInstance();

    @Override
    public List<UserRD> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserRD getById(UUID id) {
        return mapper.toDto(repository.findById(id));
    }

    @Override
    public UserRD update(UUID uuid, UserWD dto) {
        User user = mapper.toEntity(dto);
        user = repository.update(user);
        return mapper.toDto(user);
    }

    @Override
    public UserRD create(UserWD dto) {
        User user = mapper.toEntity(dto);
        user = repository.create(user);
        return mapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
