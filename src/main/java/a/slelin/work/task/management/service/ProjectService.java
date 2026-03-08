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
                .stream().map(tasks ? projectMapper::toDtoWithTasks : projectMapper::toDto).toList();
    }

    @Override
    public ProjectRD getById(@NotNull Long id) {
        return getById(id, false);
    }

    public ProjectRD getById(@NotNull Long id, boolean tasks) {
        Optional<Project> projectOptional;

        if (tasks) {
            projectOptional = projectRepository.findByIdWithTasks(id);
        } else {
            projectOptional = projectRepository.findById(id);
        }

        Project project = projectOptional
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        return tasks ? projectMapper.toDtoWithTasks(project) : projectMapper.toDto(project);
    }

    public List<TaskRD> getProjectTasks(@NotNull Long id) {
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
    public ProjectRD update(@NotNull Long id, @NotNull @Valid ProjectWD dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        Project updatedProject = projectMapper.toEntity(dto);
        updatedProject.setId(id);
        updatedProject.setTasks(project.getTasks());
        updatedProject.setUser(project.getUser());
        updatedProject = projectRepository.update(updatedProject);
        return projectMapper.toDto(updatedProject);
    }

    @Override
    public ProjectRD patch(@NotNull Long id, @NotNull @Valid ProjectWD dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        project = projectMapper.patch(project, dto);
        project = projectRepository.update(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void delete(@NotNull Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        projectRepository.deleteById(id);
    }

    public void deleteTasks(@NotNull Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        taskRepository.deleteByProject(id);
    }
}
