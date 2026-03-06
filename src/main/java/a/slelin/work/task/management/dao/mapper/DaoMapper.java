package a.slelin.work.task.management.dao.mapper;

import a.slelin.work.task.management.entity.Entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DaoMapper<E extends Entity<ID>, ID extends Serializable> {

    List<E> map(ResultSet rs) throws SQLException;

    default E mapOne(ResultSet rs) throws SQLException {
        return map(rs).getFirst();
    }

    ID mapId(ResultSet rs) throws SQLException;
}
