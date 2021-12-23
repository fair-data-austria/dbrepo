package at.tuwien.repository.jpa;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

    Optional<Query> findByDatabaseAndId(Database database, Long id);

}

