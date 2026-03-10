package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.SheetDto;
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
    public SheetDto<ProjectRD> getAll(@NotNull @Valid Pageable pageable) {
        return getAll(pageable, false);
    }

    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> getAll(@NotNull @Valid Pageable pageable, boolean tasks) {
        return SheetDto.of(projectRepository.findAll(pageable),
                tasks ? projectMapper::toDtoWithTasks : projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> search(@NotNull @Valid Pageable pageable,
                                      @NotNull @Valid FilterChain filters) {
        return search(pageable, filters, false);
    }

    @Transactional(readOnly = true)
    public SheetDto<ProjectRD> search(@NotNull @Valid Pageable pageable,
                                      @NotNull @Valid FilterChain filters,
                                      boolean tasks) {
        Specification<Project> specification = FilterUtil.toSpecification(filters);
        return SheetDto.of(projectRepository.findAll(specification, pageable),
                tasks ? projectMapper::toDtoWithTasks : projectMapper::toDto);
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
    public SheetDto<TaskRD> getProjectTasks(@NotNull @Valid Pageable pageable, @NotNull Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundByIdException(Project.class, id);
        }

        return SheetDto.of(taskRepository.findByProjectId(id, pageable), taskMapper::toDto);
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
