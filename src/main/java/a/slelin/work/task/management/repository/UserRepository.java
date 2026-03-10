package a.slelin.work.task.management.repository;

import a.slelin.work.task.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>,
        PagingAndSortingRepository<User, UUID>, JpaSpecificationExecutor<User> {
}
