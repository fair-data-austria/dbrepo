package at.tuwien.seeder.impl;

import at.tuwien.entities.container.Container;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.seeder.Seeder;
import at.tuwien.service.ContainerService;
import com.sun.security.auth.UserPrincipal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
public class ContainerSeederImpl extends AbstractSeeder implements Seeder {

    private final ContainerRepository containerRepository;
    private final ContainerService containerService;

    @Autowired
    public ContainerSeederImpl(ContainerRepository containerRepository, ContainerService containerService) {
        this.containerRepository = containerRepository;
        this.containerService = containerService;
    }

    @SneakyThrows
    @Override
    public void seed() {
        if (containerRepository.existsById(CONTAINER_1_ID)) {
            log.warn("Already seeded. Skip.");
            return;
        }
        /* seed */
        final Container container1 = containerService.create(CONTAINER_1_CREATE_DTO);
        log.info("Created container id {}", container1.getId());
        final Container container1start = containerService.start(CONTAINER_1_ID);
        log.info("Started container id {}", container1start.getId());
        final Container container2 = containerService.create(CONTAINER_2_CREATE_DTO);
        log.info("Created container id {}", container2.getId());
        final Container container2start = containerService.start(CONTAINER_2_ID);
        log.info("Started container id {}", container2start.getId());
    }

}
