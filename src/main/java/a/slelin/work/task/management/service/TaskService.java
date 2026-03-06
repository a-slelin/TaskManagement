package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.dto.TaskDto;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Task;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskService implements Service<Long, TaskDto> {

    @Getter
    private final static TaskService instance = new TaskService();

    private final static TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    private final static TaskDao repository = TaskDao.getInstance();

    @Override
    public List<TaskDto> getAll() {
        return repository.getAll()
                .stream().map(mapper::toDto)
                .toList();
    }

    @Override
    public TaskDto getById(Long id) {
        return mapper.toDto(repository.getById(id));
    }

    @Override
    public Long create(TaskDto dto) {
        Task task = mapper.toEntity(dto);
        task = repository.create(task);
        return task.getId();
    }

    @Override
    public TaskDto update(TaskDto dto) {
        Task task = mapper.toEntity(dto);
        task = repository.update(task);
        return mapper.toDto(task);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
