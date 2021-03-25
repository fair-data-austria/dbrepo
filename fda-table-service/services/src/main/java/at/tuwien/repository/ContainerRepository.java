package at.tuwien.repository;

import at.tuwien.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    Container findContainerByContainerId(String containerId);

}
