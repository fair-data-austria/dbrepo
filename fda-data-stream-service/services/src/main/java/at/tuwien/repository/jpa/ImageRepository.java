package at.tuwien.repository.jpa;

import at.tuwien.entities.container.image.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ContainerImage, Long> {
}
