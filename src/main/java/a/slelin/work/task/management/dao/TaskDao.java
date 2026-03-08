package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.Task;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDao implements Dao<Task, Long> {

    @Getter
    private final static TaskDao instance = new TaskDao();

    private final static EntityManager em = JpaUtil.getEntityManager();

    @Override
    public List<Task> findAll() {
        List<Task> tasks;

        try {
            em.getTransaction().begin();
            tasks = em.createQuery("""
                            SELECT t
                            FROM Task t
                            ORDER BY t.id
                            """, Task.class)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return tasks;
    }

    @Override
    public Task findById(@NotNull Long id) {
        Task task;

        try {
            em.getTransaction().begin();
            task = em.find(Task.class, id);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        if (task == null) {
            throw new EntityNotFoundByIdException(Task.class, id);
        }

        return task;
    }

    public List<Task> findByProject(@NotNull Long id) {
        List<Task> tasks;

        try {
            em.getTransaction().begin();
            tasks = em.createQuery("""
                            SELECT t
                            FROM Task t
                            WHERE t.project.id = :id
                            ORDER BY t.id
                            """, Task.class)
                    .setParameter("id", id)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return tasks;
    }

    @Override
    public boolean existsById(@NotNull Long id) {
        long count;

        try {
            em.getTransaction().begin();
            count = em.createQuery("""
                            SELECT COUNT(t)
                            FROM Task t
                            WHERE t.id = :id
                            """, Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return count > 0;
    }

    @Override
    public Task create(@NotNull Task task) {
        try {
            em.getTransaction().begin();
            em.persist(task);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return task;
    }

    @Override
    public Task update(@NotNull Task task) {
        try {
            em.getTransaction().begin();
            task = em.merge(task);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return task;
    }

    @Override
    public void delete(@NotNull Task task) {
        try {
            em.getTransaction().begin();
            em.remove(em.merge(task));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        Task task = findById(id);
        delete(task);
    }

    public void deleteByProject(@NotNull Long id) {
        try {
            em.getTransaction().begin();
            em.createQuery("""
                            DELETE
                            FROM Task t
                            WHERE t.project.id = :id
                            """)
                    .setParameter("id", id)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
