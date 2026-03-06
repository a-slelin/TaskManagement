package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.Entity;

import java.io.Serializable;
import java.util.List;

public interface Dao<E extends Entity<ID>, ID extends Serializable> {

    List<E> getAll();

    E getById(ID id);

    E create(E entity);

    E update(E entity);

    void delete(ID id);

    default void delete(E entity) {
        delete(entity.getId());
    }
}
