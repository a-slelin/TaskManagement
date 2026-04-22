package a.slelin.work.task.management.api.repository;

import a.slelin.work.task.management.api.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>,
        PagingAndSortingRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    Page<Project> findByUser(UUID id, Pageable pageable);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Query("""
            SELECT EXISTS (SELECT 1 FROM Project p
                           WHERE p.id = :project AND p.user = :user)
            """)
    boolean isProjectOfUser(@Param("user") UUID user, @Param("project") Long project);

    void deleteByUser(UUID id);
}
