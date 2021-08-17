package at.tuwien.repository.jpa;

import at.tuwien.entities.container.image.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ContainerImage, Long> {

    Optional<ContainerImage> findByRepositoryAndTag(String repository, String tag);

}
