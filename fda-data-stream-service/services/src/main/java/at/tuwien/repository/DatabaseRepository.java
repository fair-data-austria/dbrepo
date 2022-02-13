package at.tuwien.repository;

import at.tuwien.entities.database.Database;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseRepository extends JpaRepository<Database, Long> {
}
