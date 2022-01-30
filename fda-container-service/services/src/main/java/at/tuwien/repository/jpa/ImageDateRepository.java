package at.tuwien.repository.jpa;

import at.tuwien.entities.container.image.ContainerImageDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDateRepository extends JpaRepository<ContainerImageDate, Long> {

}
