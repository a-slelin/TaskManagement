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
            throw new RuntimeException("ProjectDao.findAll() failed.", e);
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
        } catch (EntityNotFoundByIdException e) {
            throw e;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("ProjectDao.findById() failed.", e);
        }

        return project;
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
            throw new RuntimeException("ProjectDao.existsById() failed.", e);
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
            throw new RuntimeException("ProjectDao.create() failed.", e);
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
            throw new RuntimeException("ProjectDao.update() failed.", e);
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
            throw new RuntimeException("ProjectDao.delete() failed.", e);
        }
    }

    @Override
    public void delete(Long id) {
        Project project = findById(id);
        delete(project);
    }
}
