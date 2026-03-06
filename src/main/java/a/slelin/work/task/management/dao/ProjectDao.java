package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.dao.mapper.ProjectDaoMapper;
import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.util.DatabaseUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Statement;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDao implements Dao<Project, Long> {

    private final static ProjectDaoMapper mapper = ProjectDaoMapper.getInstance();

    @Getter
    private final static ProjectDao instance = new ProjectDao();

    @Override
    public List<Project> getAll() {
        String sql = """
                SELECT *
                FROM project
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            return mapper.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении всех проектов.", e);
        }
    }

    @Override
    public Project getById(Long id) {
        String sql = """
                SELECT *
                FROM project
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return mapper.mapOne(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении проекта id = %d.".formatted(id), e);
        }
    }

    @Override
    public Project create(Project entity) {
        String sql = """
                INSERT INTO project (name, description, owner_id)
                VALUES (?, ?, ?)
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setObject(3, entity.getUser().getId());

            statement.executeUpdate();
            Long id = mapper.mapId(statement.getGeneratedKeys());
            entity.setId(id);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании проекта.", e);
        }
    }

    @Override
    public Project update(Project entity) {
        String sql = """
                UPDATE project
                SET name = ?, description = ?
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getId());

            statement.executeUpdate();

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении проекта.", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM project
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении проекта id = %d.".formatted(id), e);
        }
    }
}
