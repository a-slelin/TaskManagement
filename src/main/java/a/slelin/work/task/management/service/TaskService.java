package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    public TaskRD getById(@NotNull @Min(1) Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        return mapper.toDto(task);
    }

    @Override
    public TaskRD create(@NotNull @Valid TaskWD dto) {
        throw new UnsupportedOperationException();
    }

    public TaskRD create(@NotNull @Min(1) Long projectId, @NotNull @Valid TaskWD dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, projectId));

        Task task = mapper.toEntity(dto);
        task.setProject(project);
        task = repository.create(task);
        return mapper.toDto(task);
    }

    @Override
    public TaskRD update(@NotNull @Min(1) Long id, @NotNull @Valid TaskWD dto) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        Task task = mapper.toEntity(dto);
        task.setId(id);
        task = repository.update(task);
        return mapper.toDto(task);
    }

    @Override
    public void delete(@NotNull @Min(1) Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        repository.deleteById(id);
    }
}
