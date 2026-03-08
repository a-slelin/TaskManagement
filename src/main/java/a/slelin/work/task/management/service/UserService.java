package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.UserRD;
import a.slelin.work.task.management.dto.UserWD;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.dto.mapper.UserMapper;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Transactional
@ApplicationScoped
public class UserService implements Service<UUID, UserRD, UserWD> {

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private ProjectDao projectRepository;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserDao userRepository;

    @Override
    public List<UserRD> getAll() {
        return getAll(false, false);
    }

    public List<UserRD> getAll(boolean projects) {
        return getAll(projects, false);
    }

    public List<UserRD> getAll(boolean projects, boolean tasks) {
        List<User> users;
        Function<User, UserRD> map;

        if (projects) {
            users = tasks ? userRepository.findAllWithProjectsAndTasks()
                    : userRepository.findAllWithProjects();
            map = tasks ? userMapper::toDtoWithProjectsAndTasks : userMapper::toDtoWithProjects;
        } else {
            users = userRepository.findAll();
            map = userMapper::toDto;
        }

        return users.stream()
                .map(map)
                .toList();
    }

    @Override
    public UserRD getById(@NotNull UUID id) {
        return getById(id, false, false);
    }

    public UserRD getById(@NotNull UUID id, boolean projects) {
        return getById(id, projects, false);
    }

    public UserRD getById(@NotNull UUID id, boolean projects, boolean tasks) {
        Optional<User> userOptional;
        Function<User, UserRD> map;

        if (projects) {
            userOptional = tasks ? userRepository.findByIdWithProjectsAndTasks(id)
                    : userRepository.findByIdWithProjects(id);
            map = tasks ? userMapper::toDtoWithProjectsAndTasks : userMapper::toDtoWithProjects;

        } else {
            userOptional = userRepository.findById(id);
            map = userMapper::toDto;
        }

        User user = userOptional
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return map.apply(user);
    }

    public List<ProjectRD> getUserProjects(@NotNull UUID id) {
        return getUserProjects(id, false);
    }

    public List<ProjectRD> getUserProjects(@NotNull UUID id, boolean tasks) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        return (tasks ? projectRepository.findByUserWithTasks(id) : projectRepository.findByUser(id))
                .stream().map(projectMapper::toDto).toList();
    }

    @Override
    public UserRD create(@NotNull @Valid UserWD dto) {
        User user = userMapper.toEntity(dto);
        user = userRepository.create(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserRD update(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        updatedUser.setProjects(user.getProjects());
        updatedUser = userRepository.update(updatedUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserRD patch(@NotNull UUID id, @NotNull @Valid UserWD dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        user = userMapper.patch(user, dto);
        user = userRepository.update(user);
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

        projectRepository.deleteByUser(id);
    }
}
