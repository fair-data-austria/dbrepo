package at.tuwien.repository.jpa;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

    List<Query> findByDatabase(Database database);

    Optional<Query> findByDatabaseAndId(Database database, Long id);
}
