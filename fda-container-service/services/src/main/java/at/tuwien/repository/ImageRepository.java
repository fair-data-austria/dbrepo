package at.tuwien.repository;

import at.tuwien.entity.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ContainerImage, Long> {

    Optional<ContainerImage> findByRepositoryAndTag(String repository, String tag);

}
