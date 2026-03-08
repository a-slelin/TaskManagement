package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TaskDao implements Dao<Task, Long> {

    @PersistenceContext(unitName = "TaskManagementPU")
    private EntityManager em;

    @Override
    public List<Task> findAll() {
        return em.createQuery("""
                SELECT t
                FROM Task t
                """, Task.class).getResultList();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(em.find(Task.class, id));
    }

    public List<Task> findByProject(Project project) {
        return findByProject(project.getId());
    }

    public List<Task> findByProject(Long id) {
        return em.createQuery("""
                SELECT t
                FROM Task t
                WHERE t.project.id = :id
                """, Task.class).setParameter("id", id).getResultList();
    }

    @Override
    public long count() {
        return em.createQuery("""
                SELECT COUNT(t)
                FROM Task t
                """, Long.class).getSingleResult();
    }

    @Override
    public long countById(Long id) {
        return em.createQuery("""
                SELECT COUNT(t)
                FROM Task t
                WHERE t.id = :id
                """, Long.class).setParameter("id", id).getSingleResult();
    }

    @Override
    public Task create(Task task) {
        em.persist(task);
        return task;
    }

    @Override
    public Task update(Task task) {
        return em.merge(task);
    }

    @Override
    public void delete(Task task) {
        em.remove(task);
    }

    @Override
    public void deleteById(Long id) {
        em.createQuery("""
                DELETE
                FROM Task t
                WHERE t.id = :id
                """).setParameter("id", id).executeUpdate();
    }

    public void deleteByProject(Project project) {
        deleteByProject(project.getId());
    }

    public void deleteByProject(Long id) {
        em.createQuery("""
                        DELETE
                        FROM Task t
                        WHERE t.project.id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        em.createQuery("""
                DELETE
                FROM Task t
                """).executeUpdate();
    }
}
