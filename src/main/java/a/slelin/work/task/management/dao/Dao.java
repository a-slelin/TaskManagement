package a.slelin.work.task.management.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Dao<E, ID extends Serializable> {

    List<E> findAll();

    Optional<E> findById(ID id);

    long count();

    long countById(ID id);

    default boolean existsById(ID id) {
        return countById(id) > 0;
    }

    E create(E e);

    E update(E e);

    void delete(E e);

    void deleteById(ID id);

    void deleteAll();
}
