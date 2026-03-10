package a.slelin.work.task.management.repository;

import a.slelin.work.task.management.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>,
        PagingAndSortingRepository<Project, Long> {

    Page<Project> findByUserId(UUID id, Pageable pageable);

    void deleteByUserId(UUID id);
}
