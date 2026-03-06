package a.slelin.work.task.management.dao;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public interface Dao<E, ID extends Serializable> {

    List<E> findAll();

    E findById(@NotNull ID id);

    boolean existsById(@NotNull ID id);

    E create(@NotNull E e);

    E update(@NotNull E e);

    void delete(@NotNull E e);
}
