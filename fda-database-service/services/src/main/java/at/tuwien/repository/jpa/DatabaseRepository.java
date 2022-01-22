package at.tuwien.repository.jpa;

import at.tuwien.entities.database.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatabaseRepository extends JpaRepository<Database, Long> {

    @Query("select d from Database d where d.container.id = :containerId")
    List<Database> findAllByContainerId(@Param("containerId") Long containerId);

}
