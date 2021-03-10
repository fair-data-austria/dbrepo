package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import services.entities.DatabaseContainer;

@Repository
public interface MetadataRepository extends JpaRepository<DatabaseContainer, String> {
}
