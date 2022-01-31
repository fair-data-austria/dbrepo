package at.tuwien.repository.jpa;

import at.tuwien.entities.identifier.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {

}
