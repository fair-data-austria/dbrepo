package at.tuwien.repository.jpa;

import at.tuwien.entities.database.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

    @org.springframework.data.jpa.repository.Query(value = "select q from Query q where q.table.database.id = :id")
    List<Query> findAllByDatabaseId(@Param("id") Long id);

}
