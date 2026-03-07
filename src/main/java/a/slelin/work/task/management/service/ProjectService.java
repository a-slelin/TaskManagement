package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.ProjectDao;
import a.slelin.work.task.management.dto.ProjectRD;
import a.slelin.work.task.management.dto.ProjectWD;
import a.slelin.work.task.management.dto.mapper.ProjectMapper;
import a.slelin.work.task.management.entity.Project;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectService implements Service<Long, ProjectRD, ProjectWD> {

    @Getter
    private final static ProjectService instance = new ProjectService();

    private final static ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    private final static ProjectDao repository = ProjectDao.getInstance();

    @Override
    public List<ProjectRD> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ProjectRD getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    @Override
    public ProjectRD create(ProjectWD dto) {
        Project project = mapper.toEntity(dto);
        project = repository.create(project);
        return mapper.toDto(project);
    }

    @Override
    public ProjectRD update(Long id, ProjectWD dto) {
        Project project = mapper.toEntity(dto);
        project.setId(id);
        project = repository.update(project);
        return mapper.toDto(project);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
