package at.tuwien.repository;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    Container findByHash(String id);

    @Modifying
    @Query(value = "update Container set image = null where image.id = :image_id")
    void detachImage(@Param("image_id") Long imageId);

}
