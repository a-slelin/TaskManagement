package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.dao.UserDao;
import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@ApplicationScoped
public class ProjectService implements Service<Long, ProjectRD, ProjectWD> {

    @Inject
    private UserDao userRepository;

    @Inject
    private TaskMapper taskMapper;

    @Inject
    private TaskDao taskRepository;

    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private ProjectDao projectRepository;

    @Override
    public List<ProjectRD> getAll() {
        return getAll(false);
    }

    public List<ProjectRD> getAll(boolean tasks) {
        return (tasks ? projectRepository.findAllWithTasks() : projectRepository.findAll())
                .stream().map(projectMapper::toDto).toList();
    }

    @Override
    public ProjectRD getById(@NotNull @Min(1) Long id) {
        return getById(id, false);
    }

    public ProjectRD getById(@NotNull @Min(1) Long id, boolean tasks) {
        Optional<Project> projectOptional;

        if (tasks) {
            projectOptional = projectRepository.findByIdWithTasks(id);
        } else {
            projectOptional = projectRepository.findById(id);
        }

        Project project = projectOptional
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        return projectMapper.toDto(project);
    }

    public List<TaskRD> getProjectTasks(@NotNull @Min(1) Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        return taskRepository.findByProject(id).stream()
                .map(taskMapper::toDto).toList();
    }

    @Override
    public ProjectRD create(@NotNull @Valid ProjectWD dto) {
        throw new UnsupportedOperationException();
    }

    public ProjectRD create(@NotNull UUID userId, @NotNull @Valid ProjectWD dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundByIdException(User.class, userId));

        Project project = projectMapper.toEntity(dto);
        project.setUser(user);
        project = projectRepository.create(project);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectRD update(@NotNull @Min(1) Long id, @NotNull @Valid ProjectWD dto) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        Project project = projectMapper.toEntity(dto);
        project.setId(id);
        project = projectRepository.update(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void delete(@NotNull @Min(1) Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        projectRepository.deleteById(id);
    }

    public void deleteTasks(@NotNull @Min(1) Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        taskRepository.deleteByProject(id);
    }
}
