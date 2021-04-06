package at.tuwien.repository;

import at.tuwien.entity.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {

}

