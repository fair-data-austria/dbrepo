package at.tuwien.repository.jpa;

import at.tuwien.entities.database.table.columns.TableColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableColumnRepository extends JpaRepository<TableColumn, Long> {

}
