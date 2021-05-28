package at.tuwien.repository;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findByDatabase(Database database);

    Optional<Table> findByDatabaseAndId(Database database, Long tableId);

}
