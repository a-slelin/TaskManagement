package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dao.TaskDao;
import a.slelin.work.task.management.dto.TaskRD;
import a.slelin.work.task.management.dto.TaskWD;
import a.slelin.work.task.management.dto.mapper.TaskMapper;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskService implements Service<Long, TaskRD, TaskWD> {

    @Getter
    private final static TaskService instance = new TaskService();

    private final static TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    private final static TaskDao repository = TaskDao.getInstance();

    @Override
    public List<TaskRD> getAll() {
        return repository.findAll()
                .stream().map(mapper::toDto)
                .toList();
    }

    @Override
    public TaskRD getById(Long id) {
        return mapper.toDto(repository.findById(id));
    }

    @Override
    public TaskRD create(TaskWD dto) {
        Task task = mapper.toEntity(dto);
        task = repository.create(task);
        return mapper.toDto(task);
    }

    @Override
    public TaskRD update(Long id, TaskWD dto) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        Task task = mapper.toEntity(dto);
        task.setId(id);
        task = repository.update(task);
        return mapper.toDto(task);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
