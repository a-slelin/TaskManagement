package a.slelin.work.task.management.dao.mapper;

import a.slelin.work.task.management.entity.Gender;
import a.slelin.work.task.management.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDaoMapper implements DaoMapper<User, UUID> {

    @Getter
    public static final UserDaoMapper instance = new UserDaoMapper();

    @Override
    public List<User> map(ResultSet rs) throws SQLException {
        List<User> result = new ArrayList<>();

        while (rs.next()) {
            User user = User.builder()
                    .id(rs.getObject("id", UUID.class))
                    .username(rs.getString("username"))
                    .password(rs.getString("password"))
                    .gender(Gender.of(rs.getString("gender")))
                    .phone(rs.getString("phone"))
                    .email(rs.getString("email"))
                    .build();
            result.add(user);
        }

        return result;
    }

    @Override
    public UUID mapId(ResultSet rs) throws SQLException {
        List<UUID> result = new ArrayList<>();

        while (rs.next()) {
            result.add(rs.getObject("id", UUID.class));
        }

        return result.getFirst();
    }
}
