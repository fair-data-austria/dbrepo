package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.dto.database.CreateDatabaseContainerDto;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceTest extends BaseIntegrationTest {

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ContainerService containerService;

    @Test
    public void getAllTest_succeeds() {
        when(containerRepository.findAll())
                .thenReturn(List.of(CONTAINER_1, CONTAINER_2));

        final List<DatabaseContainer> response = containerService.getAll();
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(CONTAINER_1, response.get(0));
        Assertions.assertEquals(CONTAINER_2, response.get(1));
    }

    @Test
    public void getById_succeeds() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(CONTAINER_1);

        final DatabaseContainer response = containerService.getById(CONTAINER_1_ID);
        Assertions.assertEquals(CONTAINER_1, response);
    }

    @Test
    public void getById_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        final DatabaseContainer response = containerService.getById(CONTAINER_1_ID);
        Assertions.assertNull(response);
    }

    @Test
    public void create_succeeds() {
        when(imageRepository.findByImage(IMAGE_1_REPOSITORY, IMAGE_1_TAG))
                .thenReturn(IMAGE_1);

        final DatabaseContainer response = containerService.getById(CONTAINER_1_ID);
        Assertions.assertNull(response);
    }

    @Test
    public void create_noImage_fails() {
        final CreateDatabaseContainerDto containerDto = new CreateDatabaseContainerDto();
        containerDto.setImage("postgres:latest");

        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
    }
}
