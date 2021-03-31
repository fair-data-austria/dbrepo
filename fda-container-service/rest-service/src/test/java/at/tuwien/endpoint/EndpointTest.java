package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.entity.Container;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import at.tuwien.service.ContainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointTest extends BaseIntegrationTest {

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ContainerService containerService;

    @Test
    public void listAllDatabases_succeeds() {
        when(containerService.getAll())
                .thenReturn(List.of(CONTAINER_1, CONTAINER_2));

        final List<Container> response = containerService.getAll();

        assertEquals(2, response.size());
        assertEquals(CONTAINER_1_ID, response.get(0).getId());
        assertEquals(CONTAINER_1_NAME, response.get(0).getName());
        assertEquals(CONTAINER_2_ID, response.get(1).getId());
        assertEquals(CONTAINER_2_NAME, response.get(1).getName());
    }

    @Test
    public void create_succeeds() throws ImageNotFoundException, DockerClientException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(containerService.create(request))
                .thenReturn(CONTAINER_1);

        final Container response = containerService.create(request);

        assertNotNull(response);
        assertEquals(response, CONTAINER_1);
        assertEquals(response.getImage(), CONTAINER_1_IMAGE);
    }

    @Test
    public void create_fails() throws ImageNotFoundException, DockerClientException {
        final ContainerCreateRequestDto request = ContainerCreateRequestDto.builder()
                .name(CONTAINER_1_NAME)
                .repository(CONTAINER_1_IMAGE.getRepository())
                .tag(CONTAINER_1_IMAGE.getTag())
                .name(CONTAINER_1_DATABASE)
                .build();
        when(containerService.create(request))
                .thenReturn(CONTAINER_1);
        when(imageRepository.findByRepositoryAndTag(IMAGE_1_REPOSITORY, IMAGE_1_TAG))
                .thenReturn(null);
    }

    @Test
    public void findById_succeeds() {
        //
    }

    @Test
    public void findById_fails() {
        //
    }

    @Test
    public void change_succeeds() {
        //
    }

    @Test
    public void change_fails() {
        //
    }

    @Test
    public void delete_succeeds() {
        //
    }

    @Test
    public void delete_fails() {
        //
    }
}
