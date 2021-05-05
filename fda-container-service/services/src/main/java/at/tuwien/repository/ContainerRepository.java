package at.tuwien.repository;

import at.tuwien.entities.container.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    Container findByHash(String id);

}
