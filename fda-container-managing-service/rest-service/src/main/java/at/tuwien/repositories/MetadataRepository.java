package at.tuwien.repositories;

import at.tuwien.entities.DatabaseContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<DatabaseContainer, String> {
}
