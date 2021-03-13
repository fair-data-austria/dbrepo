package at.tuwien.repository;

import at.tuwien.entity.ContainerImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ContainerImage, Long> {

    @Query(value = "select distinct tag from ContainerImage where repository = :repo and tag = :tag")
    ContainerImage findByImage(@Param("repo") String repository, @Param("tag") String tag);

}
