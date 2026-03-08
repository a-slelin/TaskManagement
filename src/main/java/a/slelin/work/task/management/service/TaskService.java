package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.exception.TaskSetProjectException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Transactional
@ApplicationScoped
public class TaskService implements Service<Long, TaskRD, TaskWD> {

    @Inject
    private ProjectDao projectRepository;

    @Inject
    private TaskMapper mapper;

    @Inject
    private TaskDao repository;

    @Override
    public List<TaskRD> getAll() {
        return repository.findAll()
                .stream().map(mapper::toDto)
                .toList();
    }

    @Override
    public TaskRD getById(@NotNull Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        return mapper.toDto(task);
    }

    @Override
    public TaskRD create(@NotNull @Valid TaskWD dto) {
        throw new UnsupportedOperationException();
    }

    public TaskRD create(@NotNull Long projectId, @NotNull @Valid TaskWD dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, projectId));

        Task task = mapper.toEntity(dto);
        task.setProject(project);
        task = repository.create(task);
        return mapper.toDto(task);
    }

    @Override
    public TaskRD update(@NotNull Long id, @NotNull @Valid TaskWD dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        Task updatedTask = mapper.toEntity(dto);
        updatedTask.setId(id);
        updatedTask.setProject(task.getProject());
        updatedTask = repository.update(updatedTask);
        return mapper.toDto(updatedTask);
    }

    @Override
    public TaskRD patch(@NotNull Long id, @NotNull @Valid TaskWD dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        task = mapper.patch(task, dto);
        task = repository.update(task);
        return mapper.toDto(task);
    }

    public TaskRD drawToProject(@NotNull Long projectId, @NotNull Long taskId) {
        Project newProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, projectId));

        Task task = repository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, taskId));

        Project oldProject = task.getProject();
        if (newProject.equals(oldProject)) {
            throw new TaskSetProjectException("Try set the same project.");
        }

        if (!newProject.getUser().equals(oldProject.getUser())) {
            throw new TaskSetProjectException("Try set project from other user.");
        }

        task.setProject(newProject);
        task = repository.update(task);
        return mapper.toDto(task);
    }

    @Override
    public void delete(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        repository.deleteById(id);
    }
}
