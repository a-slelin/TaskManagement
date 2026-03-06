package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.User;
import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao implements Dao<User, UUID> {

    @Getter
    private final static UserDao instance = new UserDao();

    private final static EntityManager em = JpaUtil.getEntityManager();

    @Override
    public List<User> findAll() {
        List<User> users;

        try {
            em.getTransaction().begin();
            users = em.createQuery("""
                            SELECT u
                            FROM User u JOIN Project p
                            ON u.id = p.user.id JOIN Task t
                            ON p.id = t.project.id
                            """, User.class)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.findAll() failed.", e);
        }

        return users;
    }

    public User getUserById(String id) {
        return findById(UUID.fromString(id));
    }

    @Override
    public User findById(@NotNull UUID id) {
        User user;

        try {
            em.getTransaction().begin();
            user = em.find(User.class, id);

            if (user == null) {
                throw new EntityNotFoundByIdException(User.class, id);
            }

            Hibernate.initialize(user.getProjects());
            user.getProjects()
                    .forEach(project -> Hibernate.initialize(project.getTasks()));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.findById() failed.", e);
        }

        return user;
    }

    @Override
    public boolean existsById(@NotNull UUID id) {
        int count;

        try {
            em.getTransaction().begin();
            count = em.createQuery("""
                            SELECT COUNT(u)
                            FROM User u
                            WHERE u.id = :id
                            """, Integer.class)
                    .setParameter("id", id)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.existsById() failed.", e);
        }

        return count > 0;
    }

    @Override
    public User create(@NotNull User user) {
        try {
            em.getTransaction().begin();
            em.persist(user);
            Hibernate.initialize(user.getProjects());
            user.getProjects()
                    .forEach(project -> Hibernate.initialize(project.getTasks()));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.create() failed.", e);
        }

        return user;
    }

    @Override
    public User update(@NotNull User user) {
        try {
            em.getTransaction().begin();
            user = em.merge(user);
            Hibernate.initialize(user.getProjects());
            user.getProjects()
                    .forEach(project -> Hibernate.initialize(project.getTasks()));
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.update() failed.", e);
        }

        return user;
    }

    @Override
    public void delete(@NotNull User user) {
        try {
            em.getTransaction().begin();
            em.remove(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("UserDao.delete() failed.", e);
        }
    }
}
