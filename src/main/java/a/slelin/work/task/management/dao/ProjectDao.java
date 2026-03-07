package a.slelin.work.task.management.dao;

import a.slelin.work.task.management.entity.Project;
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
public class ProjectDao implements Dao<Project, Long> {

    @Getter
    private final static ProjectDao instance = new ProjectDao();

    private final static EntityManager em = JpaUtil.getEntityManager();

    @Override
    public List<Project> findAll() {
        List<Project> projects;

        try {
            em.getTransaction().begin();
            projects = em.createQuery("""
                    SELECT p
                    FROM Project p LEFT OUTER JOIN Task t
                    ON p.id = t.project.id
                    """, Project.class).getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return projects;
    }

    @Override
    public Project findById(@NotNull Long id) {
        Project project;

        try {
            em.getTransaction().begin();
            project = em.find(Project.class, id);

            if (project == null) {
                throw new EntityNotFoundByIdException(Project.class, id);
            }

            Hibernate.initialize(project.getTasks());
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return project;
    }

    public List<Project> findByUser(@NotNull UUID id) {
        List<Project> projects;

        try {
            em.getTransaction().begin();
            projects = em.createQuery("""
                            SELECT p
                            FROM Project p LEFT OUTER JOIN Task t
                            ON p.id = t.project.id
                            WHERE p.user.id = :id
                            """, Project.class)
                    .setParameter("id", id)
                    .getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return projects;
    }

    @Override
    public boolean existsById(@NotNull Long id) {
        long count;

        try {
            em.getTransaction().begin();
            count = em.createQuery("""
                            SELECT COUNT(p)
                            FROM Project p
                            WHERE p.id = :id
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
    public Project create(@NotNull Project project) {
        try {
            em.getTransaction().begin();
            em.persist(project);
            Hibernate.initialize(project.getTasks());
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return project;
    }

    @Override
    public Project update(@NotNull Project project) {
        try {
            em.getTransaction().begin();
            project = em.merge(project);
            Hibernate.initialize(project.getTasks());
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return project;
    }

    @Override
    public void delete(@NotNull Project project) {
        try {
            em.getTransaction().begin();
            em.remove(project);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        Project project = findById(id);
        delete(project);
    }

    public void deleteByUser(@NotNull UUID id) {
        try {
            em.getTransaction().begin();
            em.createQuery("""
                            DELETE
                            FROM Project p
                            WHERE p.user.id = :id
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
