package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.dao.mapper.UserDaoMapper;
import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.util.DatabaseUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao implements Dao<User, UUID> {

    private final static UserDaoMapper mapper = UserDaoMapper.getInstance();

    @Getter
    public static final UserDao instance = new UserDao();

    @Override
    public List<User> getAll() {
        String sql = """
                SELECT *
                FROM users
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            return mapper.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении всех пользователей.", e);
        }
    }

    @Override
    public User getById(UUID id) {
        String sql = """
                SELECT *
                FROM users
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            var resultSet = statement.executeQuery();
            return mapper.mapOne(resultSet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователя id = %s.".formatted(id.toString()), e);
        }
    }

    @Override
    public User create(User entity) {
        String sql = """
                INSERT INTO users(username, password, gender, phone, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getGender().getDisplayName());
            statement.setString(4, entity.getPhone());
            statement.setString(5, entity.getEmail());

            statement.executeUpdate();
            UUID id = mapper.mapId(statement.getGeneratedKeys());
            entity.setId(id);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании пользователя.", e);
        }
    }

    @Override
    public User update(User entity) {
        String sql = """
                UPDATE users
                SET username = ?, password = ?, gender = ?, phone = ?, email = ?
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getGender().getDisplayName());
            statement.setString(4, entity.getPhone());
            statement.setString(5, entity.getEmail());
            statement.setString(6, UUID.randomUUID().toString());

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении пользователя.", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;

        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении пользователя id = %s.".formatted(id), e);
        }
    }
}
