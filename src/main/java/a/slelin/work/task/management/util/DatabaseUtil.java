package a.slelin.work.task.management.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static a.slelin.work.task.management.util.PropertiesUtil.get;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseUtil {

    public static final String KEY_DB_HOST;
    public static final String KEY_DB_PORT;
    public static final String KEY_DB_NAME;
    public static final String KEY_DB_DRIVER;
    public static final String KEY_DB_USERNAME;
    public static final String KEY_DB_PASSWORD;

    public static Connection getConnection() {
        Connection connection;
        try {
            String url = "jdbc:%s://%s:%s/%s".formatted(
                    get(KEY_DB_DRIVER),
                    get(KEY_DB_HOST),
                    get(KEY_DB_PORT),
                    get(KEY_DB_NAME));

            connection = DriverManager.getConnection(
                    url,
                    get(KEY_DB_USERNAME),
                    get(KEY_DB_PASSWORD)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось открывать соединение с базой данных.", e);
        }

        return connection;
    }

    static {
        KEY_DB_HOST = "db.host";
        KEY_DB_PORT = "db.port";
        KEY_DB_NAME = "db.name";
        KEY_DB_DRIVER = "db.driver";
        KEY_DB_USERNAME = "db.username";
        KEY_DB_PASSWORD = "db.password";
    }
}
