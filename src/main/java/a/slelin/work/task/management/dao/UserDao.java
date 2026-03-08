package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserDao implements Dao<User, UUID> {

    @PersistenceContext(unitName = "TaskManagementPU")
    private EntityManager em;

    @Override
    public List<User> findAll() {
        return em.createQuery("""
                        SELECT u
                        FROM User u
                        """, User.class)
                .getResultList();
    }

    public List<User> findAllWithProjects() {
        return em.createQuery("""
                        SELECT u
                        FROM User u LEFT JOIN Project p
                        ON u.id = p.user.id
                        """, User.class)
                .getResultList();
    }

    public List<User> findAllWithProjectsAndTasks() {
        return em.createQuery("""
                        SELECT u
                        FROM User u LEFT JOIN Project p
                        ON u.id = p.user.id LEFT JOIN Task t
                        ON p.id = t.project.id
                        """, User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByIdWithProjects(UUID id) {
        return Optional.ofNullable(em.createQuery("""
                        SELECT u
                        FROM User u LEFT JOIN Project p
                        ON u.id = p.user.id
                        WHERE u.id = :id
                        """, User.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    public Optional<User> findByIdWithProjectsAndTasks(UUID id) {
        return Optional.ofNullable(em.createQuery("""
                        SELECT u
                        FROM User u LEFT JOIN Project p
                        ON u.id = p.user.id LEFT JOIN Task t
                        ON p.id = t.project.id
                        WHERE u.id = :id
                        """, User.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    @Override
    public long count() {
        return em.createQuery("""
                        SELECT COUNT(u)
                        FROM User u
                        """, Long.class)
                .getSingleResult();
    }

    @Override
    public long countById(UUID id) {
        return em.createQuery("""
                        SELECT COUNT(u)
                        FROM User u
                        WHERE u.id = :id
                        """, Long.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public User create(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        return em.merge(user);
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }

    @Override
    public void deleteById(UUID id) {
        em.createQuery("""
                        DELETE
                        FROM User u
                        WHERE u.id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteAll() {
        em.createQuery("""
                        DELETE
                        FROM User u
                        """)
                .executeUpdate();
    }
}
