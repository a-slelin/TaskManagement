package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.repository.ProjectRepository;
import a.slelin.work.task.management.repository.TaskRepository;
import a.slelin.work.task.management.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ProjectService implements CrudService<Long, ProjectRD, ProjectWD> {

    private final ProjectMapper projectMapper;

    private final ProjectRepository projectRepository;

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectRD> getAll() {
        return getAll(false);
    }

    @Transactional(readOnly = true)
    public List<ProjectRD> getAll(boolean tasks) {
        return projectRepository.findAll()
                .stream()
                .map(tasks ? projectMapper::toDtoWithTasks : projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectRD getById(@NotNull Long id) {
        return getById(id, false);
    }

    @Transactional(readOnly = true)
    public ProjectRD getById(@NotNull Long id, boolean tasks) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        return tasks ? projectMapper.toDtoWithTasks(project) : projectMapper.toDto(project);
    }

    @Transactional(readOnly = true)
    public List<TaskRD> getProjectTasks(@NotNull Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        return taskRepository.findByProjectId(id).stream()
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
        project = projectRepository.save(project);
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
        updatedProject = projectRepository.save(updatedProject);
        return projectMapper.toDto(updatedProject);
    }

    @Override
    public ProjectRD patch(@NotNull Long id, @NotNull @Valid ProjectWD dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, id));

        project = projectMapper.patch(project, dto);
        project = projectRepository.save(project);
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

        taskRepository.deleteByProjectId(id);
    }
}
