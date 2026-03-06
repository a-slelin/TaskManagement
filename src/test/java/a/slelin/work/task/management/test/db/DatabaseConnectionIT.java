package a.slelin.work.task.management.test.db;

import a.slelin.work.task.management.util.DatabaseUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DatabaseConnectionIT {

    @Test
    public void ping() throws SQLException {
        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement("SELECT 1");
             var resultSet = statement.executeQuery()) {

            Assertions.assertTrue(resultSet.next());
            Assertions.assertEquals(1, resultSet.getInt(1));
        }
    }
}
