package at.tuwien.repository;

import at.tuwien.entities.database.table.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {

}
