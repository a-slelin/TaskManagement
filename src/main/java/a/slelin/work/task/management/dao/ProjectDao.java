package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.Project;
import a.slelin.work.task.management.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProjectDao implements Dao<Project, Long> {

    @PersistenceContext(unitName = "TaskManagementPU")
    private EntityManager em;

    @Override
    public List<Project> findAll() {
        return em.createQuery("""
                        SELECT p
                        FROM Project p
                        """, Project.class)
                .getResultList();
    }

    public List<Project> findAllWithTasks() {
        return em.createQuery("""
                        SELECT p
                        FROM Project p LEFT OUTER JOIN Task t
                        ON p.id = t.project.id
                        """, Project.class)
                .getResultList();
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.of(em.find(Project.class, id));
    }

    public Optional<Project> findByIdWithTasks(Long id) {
        return Optional.of(em.createQuery("""
                        SELECT p
                        FROM Project p LEFT OUTER JOIN Task t
                        ON p.id = t.project.id
                        WHERE p.id = :id
                        """, Project.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    public List<Project> findByUser(User user) {
        return findByUser(user.getId());
    }

    public List<Project> findByUser(UUID id) {
        return em.createQuery("""
                        SELECT p
                        FROM Project p
                        WHERE p.user.id = :id
                        """, Project.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<Project> findByUserWithTasks(User user) {
        return findByUserWithTasks(user.getId());
    }

    public List<Project> findByUserWithTasks(UUID id) {
        return em.createQuery("""
                        SELECT p
                        FROM Project p LEFT OUTER JOIN Task t
                        ON p.id = t.project.id
                        WHERE p.user.id = :id
                        """, Project.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public long count() {
        return em.createQuery("""
                        SELECT COUNT(p)
                        FROM Project p
                        """, Long.class)
                .getSingleResult();
    }

    @Override
    public long countById(Long id) {
        return em.createQuery("""
                        SELECT COUNT(p)
                        FROM Project p
                        WHERE p.id = :id
                        """, Long.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public Project create(Project project) {
        em.persist(project);
        return project;
    }

    @Override
    public Project update(Project project) {
        return em.merge(project);
    }

    @Override
    public void delete(Project project) {
        em.remove(project);
    }

    @Override
    public void deleteById(Long id) {
        em.createQuery("""
                        DELETE
                        FROM Project p
                        WHERE p.id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void deleteByUser(User user) {
        deleteByUser(user.getId());
    }

    public void deleteByUser(UUID id) {
        em.createQuery("""
                        DELETE
                        FROM Project p
                        WHERE p.user.id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        em.createQuery("""
                        DELETE
                        FROM Project p
                        """)
                .executeUpdate();
    }
}
