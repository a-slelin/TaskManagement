package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.dao.mapper.TaskDaoMapper;
import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.util.DatabaseUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Statement;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDao implements Dao<Task, Long> {

    private static final TaskDaoMapper mapper = TaskDaoMapper.getInstance();

    @Getter
    private static final TaskDao instance = new TaskDao();

    @Override
    public List<Task> getAll() {
        String sql = """
                SELECT *
                FROM task
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            return mapper.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении всех задач.", e);
        }
    }

    @Override
    public Task getById(Long id) {
        String sql = """
                SELECT *
                FROM task
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return mapper.mapOne(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении задачи id = %d.".formatted(id), e);
        }
    }

    @Override
    public Task create(Task entity) {
        String sql = """
                INSERT INTO task (title, description, status, project_id)
                VALUES (?, ?, ?, ?)
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getStatus() == null ? null
                    : entity.getStatus().getDisplayName());
            statement.setLong(4, entity.getProject().getId());

            statement.executeUpdate();
            Long id = mapper.mapId(statement.getGeneratedKeys());
            entity.setId(id);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании задачи.", e);
        }
    }

    @Override
    public Task update(Task entity) {
        String sql = """
                UPDATE task
                SET title = ?, description = ?, status = ?
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getStatus() == null ? null
                    : entity.getStatus().getDisplayName());
            statement.setLong(4, entity.getId());

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении задачи.", e);
        }
    }

    @Override
    public void delete(Task entity) {
        String sql = """
                DELETE FROM task
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, entity.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении задачи id = %d.".formatted(entity.getId()), e);
        }
    }
}
