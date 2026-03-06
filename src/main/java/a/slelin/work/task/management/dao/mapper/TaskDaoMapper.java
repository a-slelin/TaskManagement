package a.slelin.work.task.management.dao.mapper;

import a.slelin.work.task.management.entity.Status;
import a.slelin.work.task.management.entity.Task;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDaoMapper implements DaoMapper<Task, Long> {

    @Getter
    private final static TaskDaoMapper instance = new TaskDaoMapper();

    @Override
    public List<Task> map(ResultSet rs) throws SQLException {
        List<Task> result = new ArrayList<>();

        while (rs.next()) {
            Task task = Task.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .status(Status.of(rs.getString("status")))
                    .build();
            result.add(task);
        }

        return result;
    }

    @Override
    public Task mapOne(ResultSet rs) throws SQLException {
        return map(rs).getFirst();
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
