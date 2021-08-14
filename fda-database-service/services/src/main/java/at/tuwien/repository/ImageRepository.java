package at.tuwien.repository;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ContainerImage, Long> {
}
