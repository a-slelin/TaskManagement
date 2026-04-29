package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.mapper.UserMapper;
import a.slelin.work.task.management.auth.repository.UserRepository;
import a.slelin.work.task.management.core.dto.auth.UserRD;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return userMapper.toDto(user);
    }

    public UserRD create(@NotNull @Valid UserWD dto) {
        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserRD patch(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        user = userMapper.patch(user, dto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void delete(@NotNull UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        userRepository.deleteById(id);
    }
}
