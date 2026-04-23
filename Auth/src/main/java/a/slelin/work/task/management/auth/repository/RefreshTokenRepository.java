package a.slelin.work.task.management.auth.repository;

import a.slelin.work.task.management.auth.entity.RefreshToken;
import a.slelin.work.task.management.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    @Modifying
    @Query("""
            DELETE
            FROM RefreshToken t
            WHERE t.expiryDate < CURRENT_TIMESTAMP
            """)
    void deleteAllExpired();
}
