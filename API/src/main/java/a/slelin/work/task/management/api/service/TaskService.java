package a.slelin.work.task.management.api.service;

import a.slelin.work.task.management.api.mapper.TaskMapper;
import a.slelin.work.task.management.api.entity.Project;
import a.slelin.work.task.management.api.entity.Task;
import a.slelin.work.task.management.api.repository.ProjectRepository;
import a.slelin.work.task.management.api.repository.TaskRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.TaskRD;
import a.slelin.work.task.management.core.dto.api.TaskWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.core.exception.Unique2FieldsEntityException;
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

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public TaskRD getUserTask(@NotNull @Valid UUID user,
                              @NotNull @Min(1) Long task) {

        Task entity = taskRepository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!entity.getProject().getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to get a task that is not your own.");
        }

        return taskMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SheetDto<TaskRD> searchUserTasks(@NotNull @Valid UUID user,
                                            @NotNull @Valid FilterChain filters,
                                            @NotNull @Valid Pageable pageable) {

        filters.add(Filter.of("project.user", Operation.EQ, user));

        Specification<Task> specification = FilterUtil.toSpecification(filters);

        return SheetDto.of(taskRepository.findAll(specification, pageable), taskMapper::toDto);
    }

    public TaskRD createUserTask(@NotNull @Valid UUID user,
                                 @NotNull @Min(1) Long project,
                                 @NotNull @Valid TaskWD newTask) {

        Project entity = projectRepository.findById(project)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, project));

        if (!entity.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to create a task in a project that is not your own.");
        }

        if (taskRepository.existsByProjectIdAndTitle(project, newTask.title())) {
            throw new Unique2FieldsEntityException(Task.class, "project", project, "title", newTask.title());
        }

        Task task = taskMapper.toEntity(newTask);
        task.setProject(entity);
        task = taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    public TaskRD updateUserTask(@NotNull @Valid UUID user,
                                 @NotNull @Min(1) Long task,
                                 @NotNull @Valid TaskWD updTask) {

        Task entity = taskRepository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));
        Long projectId = entity.getProject().getId();

        if (!taskRepository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to update a task that is not your own.");
        }

        if (taskRepository.existsByProjectIdAndTitle(projectId, updTask.title())) {
            throw new Unique2FieldsEntityException(Task.class, "project", projectId, "title", updTask.title());
        }

        Task updatedTask = taskMapper.toEntity(updTask);
        updatedTask.setId(task);
        updatedTask.setProject(entity.getProject());
        updatedTask = taskRepository.save(updatedTask);

        return taskMapper.toDto(updatedTask);
    }

    public TaskRD patchUserTask(@NotNull @Valid UUID user,
                                @NotNull @Min(1) Long task,
                                @NotNull @Valid TaskWD pthTask) {

        Task entity = taskRepository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));
        Long projectId = entity.getProject().getId();

        if (!taskRepository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to patch a task that is not your own.");
        }

        if (pthTask.title() != null && taskRepository.existsByProjectIdAndTitle(projectId, pthTask.title())) {
            throw new Unique2FieldsEntityException(Task.class, "project", projectId, "title", pthTask.title());
        }

        entity = taskMapper.patch(entity, pthTask);
        entity = taskRepository.save(entity);

        return taskMapper.toDto(entity);
    }

    public TaskRD drawToProject(@NotNull @Valid UUID user,
                                @NotNull @Min(1) Long newProject,
                                @NotNull @Min(1) Long task) {

        Task entity = taskRepository.findById(task)
                .orElseThrow(() -> new EntityNotFoundByIdException(Task.class, task));

        if (!taskRepository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to draw a task that is not your own.");
        }

        Project newProjectEntity = projectRepository.findById(newProject)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, newProject));

        if (!projectRepository.isProjectOfUser(user, newProject)) {
            throw new AccessDeniedException("Access denied: an attempt to draw a task to project that is not your own.");
        }

        Long oldProject = entity.getProject().getId();
        if (newProject.equals(oldProject)) {
            return taskMapper.toDto(entity);
        }

        if (taskRepository.existsByProjectIdAndTitle(newProject, entity.getTitle())) {
            throw new Unique2FieldsEntityException(Task.class, "project", newProject, "title", entity.getTitle());
        }

        entity.setProject(newProjectEntity);
        entity = taskRepository.save(entity);

        return taskMapper.toDto(entity);
    }

    public void deleteUserTask(@NotNull @Valid UUID user,
                               @NotNull @Min(1) Long task) {

        if (!taskRepository.existsById(task)) {
            throw new EntityNotFoundByIdException(Task.class, task);
        }

        if (!taskRepository.isTaskOfUser(user, task)) {
            throw new AccessDeniedException("Access denied: an attempt to delete a task that is not your own.");
        }

        taskRepository.deleteById(task);
    }
}
