package a.slelin.work.task.management.api.repository;

import a.slelin.work.task.management.api.entity.Task;
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
public interface TaskRepository extends JpaRepository<Task, Long>,
        PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Query("""
            SELECT EXISTS (SELECT 1 FROM Task t
                           WHERE t.id = :task AND t.project.user = :user)
            """)
    boolean isTaskOfUser(@Param("user") UUID userId, @Param("task") Long taskId);

    boolean existsByProjectIdAndTitle(Long projectId, String title);

    void deleteByProjectId(Long projectId);
}
