package at.tuwien.seeder;

import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.service.ContainerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Profile("seed")
public class ContainerSeeder implements Seeder {

    private final static Long CONTAINER_1_ID = 1L;
    private final static String CONTAINER_1_NAME = "fda-userdb-wetter-aus";
    private final static String CONTAINER_1_REPOSITORY = "mariadb";
    private final static String CONTAINER_1_TAG = "10.5";

    private final static Long CONTAINER_2_ID = 2L;
    private final static String CONTAINER_2_NAME = "fda-userdb-infection";
    private final static String CONTAINER_2_REPOSITORY = "mariadb";
    private final static String CONTAINER_2_TAG = "10.5";

    private final static Long CONTAINER_3_ID = 3L;
    private final static String CONTAINER_3_NAME = "fda-userdb-air";
    private final static String CONTAINER_3_REPOSITORY = "mariadb";
    private final static String CONTAINER_3_TAG = "10.5";

    private final static ContainerCreateRequestDto CONTAINER_1_CREATE_REQ = ContainerCreateRequestDto.builder()
            .name(CONTAINER_1_NAME)
            .repository(CONTAINER_1_REPOSITORY)
            .tag(CONTAINER_1_TAG)
            .build();

    private final static ContainerCreateRequestDto CONTAINER_2_CREATE_REQ = ContainerCreateRequestDto.builder()
            .name(CONTAINER_2_NAME)
            .repository(CONTAINER_2_REPOSITORY)
            .tag(CONTAINER_2_TAG)
            .build();

    private final static ContainerCreateRequestDto CONTAINER_3_CREATE_REQ = ContainerCreateRequestDto.builder()
            .name(CONTAINER_3_NAME)
            .repository(CONTAINER_3_REPOSITORY)
            .tag(CONTAINER_3_TAG)
            .build();

    private final ContainerService containerService;

    @Autowired
    public ContainerSeeder(ContainerService containerService) {
        this.containerService = containerService;
    }

    @Override
    public void seed() throws DockerClientException, ImageNotFoundException {
        if (containerService.getAll().size() > 0) {
            return;
        }
        log.debug("seeded container {}", containerService.create(CONTAINER_1_CREATE_REQ));
        log.debug("started container {}", containerService.start(CONTAINER_1_ID));
        log.debug("seeded container {}", containerService.create(CONTAINER_2_CREATE_REQ));
        log.debug("started container {}", containerService.start(CONTAINER_2_ID));
        log.debug("seeded container {}", containerService.create(CONTAINER_3_CREATE_REQ));
        log.debug("started container {}", containerService.start(CONTAINER_3_ID));
    }

}
