package at.tuwien.repository;

import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
}
