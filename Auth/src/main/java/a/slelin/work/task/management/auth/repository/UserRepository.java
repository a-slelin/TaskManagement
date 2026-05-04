package a.slelin.work.task.management.auth.repository;

import a.slelin.work.task.management.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>,
        PagingAndSortingRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query("""
            SELECT u
            FROM User u
            WHERE u.username = :factor
            OR u.phone = :factor
            OR u.email = :factor
            """)
    Optional<User> findByFactor(@Param("factor") String factor);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
