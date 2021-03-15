package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.dto.database.DatabaseContainerCreateRequestDto;
import at.tuwien.exception.ContainerNotFoundException;
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

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DockerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ContainerService containerService;

    @Test
    public void create_noImage_fails() {
        final DatabaseContainerCreateRequestDto containerDto = DatabaseContainerCreateRequestDto.builder().build();

        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
    }

    @Test
    public void create_imageNotFound_fails() {
        final DatabaseContainerCreateRequestDto containerDto = DatabaseContainerCreateRequestDto.builder().build();
        containerDto.setImage("postgres:latest");

        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
    }

    @Test
    public void stop_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.stop(CONTAINER_1_ID));
    }

    @Test
    public void remove_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.remove(CONTAINER_1_ID));
    }

    @Test
    public void start_notFound_fails() {
        when(containerRepository.findByContainerId(CONTAINER_1_ID))
                .thenReturn(null);

        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.start(CONTAINER_1_ID));
    }
}
