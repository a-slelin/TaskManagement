package a.slelin.work.task.management.api.service;

import a.slelin.work.task.management.api.mapper.ProjectMapper;
import a.slelin.work.task.management.api.mapper.TaskMapper;
import a.slelin.work.task.management.api.entity.Project;
import a.slelin.work.task.management.api.entity.Task;
import a.slelin.work.task.management.api.repository.ProjectRepository;
import a.slelin.work.task.management.api.repository.TaskRepository;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.api.ProjectRD;
import a.slelin.work.task.management.core.dto.api.ProjectWD;
import a.slelin.work.task.management.core.dto.api.TaskRD;
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
public class ProjectService {

    private final ProjectMapper projectMapper;

    private final ProjectRepository projectRepository;

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> getUserProjects(@NotNull @Valid UUID user,
                                               @NotNull @Valid Pageable pageable) {
        return SheetDto.of(projectRepository.findByUser(user, pageable), projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectRD getUserProject(@NotNull @Valid UUID user,
                                    @NotNull @Min(1) Long project) {

        Project entity = projectRepository.findById(project)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, project));

        if (!entity.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to get a project that is not your own.");
        }

        return projectMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> searchUserProjects(@NotNull @Valid UUID user,
                                                  @NotNull @Valid FilterChain filters,
                                                  @NotNull @Valid Pageable pageable) {
        filters.add(Filter.of("user", Operation.EQ, user));
        Specification<Project> specification = FilterUtil.toSpecification(filters);

        return SheetDto.of(projectRepository.findAll(specification, pageable), projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public SheetDto<TaskRD> getUserProjectTasks(@NotNull @Valid UUID user,
                                                @NotNull @Min(1) Long project,
                                                @NotNull @Valid Pageable pageable) {

        if (!projectRepository.existsById(project)) {
            throw new EntityNotFoundByIdException(Project.class, project);
        }

        if (!projectRepository.isProjectOfUser(user, project)) {
            throw new AccessDeniedException("Access denied: an attempt to get a project that is not your own.");
        }

        return SheetDto.of(taskRepository.findByProjectId(project, pageable), taskMapper::toDto);
    }

    public ProjectRD createUserProject(@NotNull @Valid UUID user,
                                       @NotNull @Valid ProjectWD newProject) {

        Project project = projectMapper.toEntity(newProject);
        project.setUser(user);
        project = projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    public ProjectRD updateUserProject(@NotNull @Valid UUID user,
                                       @NotNull @Min(1) Long project,
                                       @NotNull @Valid ProjectWD updProject) {

        Project entity = projectRepository.findById(project)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, project));

        if (!entity.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to update a project that is not your own.");
        }

        Project updatedProject = projectMapper.toEntity(updProject);
        updatedProject.setId(project);
        updatedProject.setTasks(entity.getTasks());
        updatedProject.setUser(entity.getUser());
        updatedProject = projectRepository.save(updatedProject);

        return projectMapper.toDto(updatedProject);
    }

    public ProjectRD patchUserProject(@NotNull @Valid UUID user,
                                      @NotNull @Min(1) Long project,
                                      @NotNull @Valid ProjectWD pthProject) {

        Project entity = projectRepository.findById(project)
                .orElseThrow(() -> new EntityNotFoundByIdException(Project.class, project));

        if (!entity.getUser().equals(user)) {
            throw new AccessDeniedException("Access denied: an attempt to patch a project that is not your own.");
        }

        entity = projectMapper.patch(entity, pthProject);
        entity = projectRepository.save(entity);

        return projectMapper.toDto(entity);
    }

    public void deleteUserProjects(@NotNull @Valid UUID user) {
        projectRepository.deleteByUser(user);
    }

    public void deleteUserProject(@NotNull @Valid UUID user,
                                  @NotNull @Min(1) Long project) {

        if (!projectRepository.existsById(project)) {
            throw new EntityNotFoundByIdException(Project.class, project);
        }

        if (!projectRepository.isProjectOfUser(user, project)) {
            throw new AccessDeniedException("Access denied: an attempt to delete a project that is not your own.");
        }

        projectRepository.deleteById(project);
    }

    public void deleteUserProjectTasks(@NotNull @Valid UUID user,
                                       @NotNull @Min(1) Long project) {

        if (!projectRepository.existsById(project)) {
            throw new EntityNotFoundByIdException(Task.class, project);
        }

        if (!projectRepository.isProjectOfUser(user, project)) {
            throw new AccessDeniedException("Access denied: an attempt to delete a project tasks that is not your own.");
        }

        taskRepository.deleteByProjectId(project);
    }
}
