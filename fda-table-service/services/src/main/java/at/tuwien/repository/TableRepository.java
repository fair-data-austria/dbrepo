package at.tuwien.repository;

import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

    List<Table> findByDatabase(Database database);

   Table findByDatabaseAndId(Database database, Long tableId);

}
