package a.slelin.work.task.management.api.service;

import a.slelin.work.task.management.api.mapper.TaskMapper;
import a.slelin.work.task.management.api.entity.Project;
import a.slelin.work.task.management.api.entity.Task;
import a.slelin.work.task.management.api.exception.TaskSetProjectException;
import a.slelin.work.task.management.api.repository.ProjectRepository;
import a.slelin.work.task.management.api.repository.TaskRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.util.filter.Filter;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import a.slelin.work.task.management.core.util.filter.FilterUtil;
import a.slelin.work.task.management.core.util.filter.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper mapper;

    private final TaskRepository repository;

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public TaskRD getUserTask(@NotNull @Valid UUID user,
                              @NotNull @Min(1) Long task) {

        Task entity = repository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!entity.getProject().getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to get a task that is not your own.");
        }

        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SheetDto<TaskRD> searchUserTasks(@NotNull @Valid UUID user,
                                            @NotNull @Valid FilterChain filters,
                                            @NotNull @Valid Pageable pageable) {
        filters.add(Filter.of("project.user_id", Operation.EQ, user));
        Specification<Task> specification = FilterUtil.toSpecification(filters);

        return SheetDto.of(repository.findAll(specification, pageable), mapper::toDto);
    }

    public TaskRD createUserTask(@NotNull @Valid UUID user,
                                 @NotNull @Min(1) Long project,
                                 @NotNull @Valid TaskWD newTask) {

        Project entity = projectRepository.findById(project)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, project));

        if (!entity.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to create a task in a project that is not your own.");
        }

        Task task = mapper.toEntity(newTask);
        task.setProject(entity);
        task = repository.save(task);

        return mapper.toDto(task);
    }

    public TaskRD updateUserTask(@NotNull @Valid UUID user,
                                 @NotNull @Min(1) Long task,
                                 @NotNull @Valid TaskWD updTask) {

        Task entity = repository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!repository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to update a task that is not your own.");
        }

        Task updatedTask = mapper.toEntity(updTask);
        updatedTask.setId(task);
        updatedTask.setProject(entity.getProject());
        updatedTask = repository.save(updatedTask);

        return mapper.toDto(updatedTask);
    }

    public TaskRD patchUserTask(@NotNull @Valid UUID user,
                                @NotNull @Min(1) Long task,
                                @NotNull @Valid TaskWD pthTask) {

        Task entity = repository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!repository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to patch a task that is not your own.");
        }

        entity = mapper.patch(entity, pthTask);
        entity = repository.save(entity);

        return mapper.toDto(entity);
    }

    public TaskRD drawToProject(@NotNull @Valid UUID user,
                                @NotNull @Min(1) Long newProject,
                                @NotNull @Min(1) Long task) {

        Task entity = repository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!repository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to draw a task that is not your own.");
        }

        Project newProjectEntity = projectRepository.findById(newProject)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, newProject));

        if (!projectRepository.isProjectOfUser(user, newProject)) {
            throw new TaskSetProjectException("Try set project from other user.");
        }

        Long oldProject = entity.getProject().getId();
        if (newProject.equals(oldProject)) {
            throw new TaskSetProjectException("Try set the same project.");
        }

        entity.setProject(newProjectEntity);
        entity = repository.save(entity);

        return mapper.toDto(entity);
    }

    public void deleteUserTask(@NotNull @Valid UUID user,
                               @NotNull @Min(1) Long task) {

        if (!repository.existsById(task)) {
            throw new EntityNotFoundByIdException(Task.class, task);
        }

        if (!repository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to delete a task that is not your own.");
        }

        repository.deleteById(task);
    }
}
