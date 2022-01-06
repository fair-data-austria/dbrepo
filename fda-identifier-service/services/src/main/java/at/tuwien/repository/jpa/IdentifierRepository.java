package at.tuwien.repository.jpa;

import at.tuwien.entities.identifier.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, Long> {

}
