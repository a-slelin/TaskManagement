package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.exception.TaskSetProjectException;
import a.slelin.work.task.management.repository.ProjectRepository;
import a.slelin.work.task.management.repository.TaskRepository;
import a.slelin.work.task.management.util.filter.FilterChain;
import a.slelin.work.task.management.util.filter.FilterUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class TaskService implements CrudService<Long, TaskRD, TaskWD> {

    private final TaskMapper mapper;

    private final TaskRepository repository;

    private final ProjectRepository projectRepository;

    @Override
    @Transactional(readOnly = true)
    public SheetDto<TaskRD> getAll(@NotNull @Valid Pageable pageable) {
        return SheetDto.of(repository.findAll(pageable), mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SheetDto<TaskRD> search(@NotNull @Valid Pageable pageable,
                                   @NotNull @Valid FilterChain filters) {
        Specification<Task> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(repository.findAll(specification, pageable), mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
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
        task = repository.save(task);
        return mapper.toDto(task);
    }

    @Override
    public TaskRD update(@NotNull Long id, @NotNull @Valid TaskWD dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        Task updatedTask = mapper.toEntity(dto);
        updatedTask.setId(id);
        updatedTask.setProject(task.getProject());
        updatedTask = repository.save(updatedTask);
        return mapper.toDto(updatedTask);
    }

    @Override
    public TaskRD patch(@NotNull Long id, @NotNull @Valid TaskWD dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, id));

        task = mapper.patch(task, dto);
        task = repository.save(task);
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
        task = repository.save(task);
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
