package a.slelin.work.task.management.repository;

import a.slelin.work.task.management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUserId(UUID id);

    void deleteByUserId(UUID id);
}
