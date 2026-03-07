package a.slelin.work.task.management.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mindrot.bcrypt.BCrypt;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtil {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @SuppressWarnings("unused")
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
