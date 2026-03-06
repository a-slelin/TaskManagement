package a.slelin.work.task.management.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JpaUtil {

    public final static String PERSISTENCE_UNIT_NAME;

    public final static EntityManagerFactory emf;

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    static {
        PERSISTENCE_UNIT_NAME = "TaskManagementPU";

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Initial EntityManagerFactory creation failed: " + e);
        }
    }
}
