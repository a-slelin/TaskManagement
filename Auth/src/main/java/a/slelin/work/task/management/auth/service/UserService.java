package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.mapper.UserMapper;
import a.slelin.work.task.management.auth.repository.UserRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.UserRD;
import a.slelin.work.task.management.core.dto.api.UserWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.service.CrudService;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.FilterUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService implements CrudService<UUID, UserRD, UserWD> {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public SheetDto<UserRD> getAll(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(userRepository.findAll(pageable), userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SheetDto<UserRD> search(@NotNull @Valid Pageable pageable,
                                   @NotNull @Valid FilterChain filters) {
        Specification<User> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(userRepository.findAll(specification, pageable), userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return userMapper.toDto(user);
    }

    @Override
    public UserRD create(@NotNull @Valid UserWD dto) {
        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserRD update(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public UserRD patch(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        user = userMapper.patch(user, dto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void delete(@NotNull UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        userRepository.deleteById(id);
    }
}
