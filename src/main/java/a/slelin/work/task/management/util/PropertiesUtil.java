package a.slelin.work.task.management.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtil {

    public final static String FILE_NAME;
    private final static Properties PROPERTIES;

    public static String get(String key) {
        if (key == null) {
            return null;
        }

        return PROPERTIES.getProperty(key);
    }

    static {

        PROPERTIES = new Properties();
        FILE_NAME = "application.properties";

        try (var input = PropertiesUtil.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить " + FILE_NAME + ".", e);
        }
    }
}
