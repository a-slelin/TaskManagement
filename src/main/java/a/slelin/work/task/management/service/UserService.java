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

        if (projects) {
            users = tasks ? userRepository.findAllWithProjectsAndTasks()
                    : userRepository.findAllWithProjects();
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(userMapper::toDto)
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

        if (projects) {
            userOptional = tasks ? userRepository.findByIdWithProjectsAndTasks(id)
                    : userRepository.findByIdWithProjects(id);
        } else {
            userOptional = userRepository.findById(id);
        }

        User user = userOptional
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, id));

        return userMapper.toDto(user);
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
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(User.class, id);
        }

        User user = userMapper.toEntity(dto);
        user.setId(id);
        user = userRepository.update(user);
        return userMapper.toDto(user);
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
