package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.dto.mapper.UserMapper;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.repository.ProjectRepository;
import a.slelin.work.task.management.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;
import java.util.function.Function;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService implements CrudService<UUID, UserRD, UserWD> {

    private final ProjectMapper projectMapper;

    private final ProjectRepository projectRepository;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public SheetDto<UserRD> getAll(@NotNull @Valid Pageable pageable) {
        return getAll(pageable, false, false);
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public SheetDto<UserRD> getAll(@NotNull @Valid Pageable pageable, boolean projects) {
        return getAll(pageable, projects, false);
    }

    @Transactional(readOnly = true)
    public SheetDto<UserRD> getAll(@NotNull @Valid Pageable pageable, boolean projects, boolean tasks) {
        Function<User, UserRD> map;

        if (projects) {
            map = tasks ? userMapper::toDtoWithProjectsAndTasks : userMapper::toDtoWithProjects;
        } else {
            map = userMapper::toDto;
        }

        return SheetDto.of(userRepository.findAll(pageable), map);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id) {
        return getById(id, false, false);
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id, boolean projects) {
        return getById(id, projects, false);
    }

    @Transactional(readOnly = true)
    public UserRD getById(@NotNull UUID id, boolean projects, boolean tasks) {
        Function<User, UserRD> map;

        if (projects) {
            map = tasks ? userMapper::toDtoWithProjectsAndTasks : userMapper::toDtoWithProjects;
        } else {
            map = userMapper::toDto;
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return map.apply(user);
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> getUserProjects(@NotNull @Valid Pageable pageable, @NotNull UUID id) {
        return getUserProjects(pageable, id, false);
    }

    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> getUserProjects(@NotNull @Valid Pageable pageable, @NotNull UUID id, boolean tasks) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        return SheetDto.of(projectRepository.findByUserId(id, pageable), tasks ?
                projectMapper::toDtoWithTasks : projectMapper::toDto);
    }

    @Override
    public UserRD create(@NotNull @Valid UserWD dto) {
        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserRD update(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        updatedUser.setProjects(user.getProjects());
        updatedUser = userRepository.save(updatedUser);
        return userMapper.toDto(updatedUser);
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

    public void deleteUserProjects(@NotNull UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        projectRepository.deleteByUserId(id);
    }
}
