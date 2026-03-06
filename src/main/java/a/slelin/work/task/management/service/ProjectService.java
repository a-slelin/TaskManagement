package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dto.ProjectDto;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.entity.Project;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectService implements Service<Long, ProjectDto> {

    @Getter
    private final static ProjectService instance = new ProjectService();

    private final static ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    private final static ProjectDao repository = ProjectDao.getInstance();

    @Override
    public List<ProjectDto> getAll() {
        return repository.getAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ProjectDto getById(Long id) {
        return mapper.toDto(repository.getById(id));
    }

    @Override
    public Long create(ProjectDto dto) {
        Project project = mapper.toEntity(dto);
        project = repository.create(project);
        return project.getId();
    }

    @Override
    public ProjectDto update(ProjectDto dto) {
        Project project = mapper.toEntity(dto);
        project = repository.update(project);
        return mapper.toDto(project);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
