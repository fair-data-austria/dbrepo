package at.tuwien.repository.jpa;

import at.tuwien.entities.database.table.columns.TableColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableColumnRepository extends JpaRepository<TableColumn, Long> {

    Optional<TableColumn> findByIdAndTidAndCdbid(Long id, Long tid, Long cdbid);

}
