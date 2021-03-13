package at.tuwien.repository;

import at.tuwien.entity.DatabaseContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<DatabaseContainer, Long> {

    DatabaseContainer findByContainerId(String id);

}
