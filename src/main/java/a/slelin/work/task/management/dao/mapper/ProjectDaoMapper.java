package a.slelin.work.task.management.dao.mapper;

import a.slelin.work.task.management.entity.Project;
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
public class ProjectDaoMapper implements DaoMapper<Project, Long> {

    @Getter
    private static final ProjectDaoMapper instance = new ProjectDaoMapper();

    @Override
    public List<Project> map(ResultSet rs) throws SQLException {
        List<Project> result = new ArrayList<>();

        while (rs.next()) {
            Project project = Project.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .user(User.byId(rs.getObject("owner_id", UUID.class)))
                    .build();

            result.add(project);
        }

        return result;
    }

    @Override
    public Long mapId(ResultSet rs) throws SQLException {
        List<Long> result = new ArrayList<>();

        while (rs.next()) {
            result.add(rs.getLong("id"));
        }

        return result.getFirst();
    }
}
