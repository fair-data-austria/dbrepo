package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceTest extends BaseIntegrationTest {

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ContainerService containerService;

//    @Test
//    public void getAllTest_succeeds() {
//        when(containerRepository.findAll())
//                .thenReturn(List.of(CONTAINER_1, CONTAINER_2));
//
//        final List<DatabaseContainer> response = containerService.getAll();
//        Assertions.assertEquals(2, response.size());
//        Assertions.assertEquals(CONTAINER_1, response.get(0));
//        Assertions.assertEquals(CONTAINER_2, response.get(1));
//    }
//
//    @Test
//    public void getById_succeeds() throws ContainerNotFoundException {
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//
//        final DatabaseContainer response = containerService.getById(CONTAINER_1_ID);
//        Assertions.assertEquals(CONTAINER_1, response);
//    }
//
//    @Test
//    public void getById_fails() {
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(null);
//
//        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.getById(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void create_noImage_fails() {
//        final ContainerCreateRequestDto containerDto = ContainerCreateRequestDto.builder().build();
//
//        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
//    }
//
//    @Test
//    public void create_imageNotFound_fails() {
//        final ContainerCreateRequestDto containerDto = ContainerCreateRequestDto.builder().build();
//        containerDto.setImage("postgres:latest");
//
//        Assertions.assertThrows(ImageNotFoundException.class, () -> containerService.create(containerDto));
//    }
//
//    @Test
//    public void stop_dockerClient_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.stopContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotFoundException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.stop(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void stop_notFound_fails() {
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(null);
//
//        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.stop(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void stop_dockerClient2_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.stopContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotModifiedException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.stop(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void remove_notFound_fails() {
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(null);
//
//        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.remove(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void remove_dockerClient_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.removeContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotFoundException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.remove(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void remove_dockerClient2_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.removeContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotModifiedException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.remove(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void start_notFound_fails() {
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(null);
//
//        Assertions.assertThrows(ContainerNotFoundException.class, () -> containerService.start(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void start_dockerClient_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.removeContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotFoundException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.start(CONTAINER_1_ID));
//    }
//
//    @Test
//    public void start_dockerClient2_fails() {
//        final DockerClient dockerClient = mock(DockerClient.class);
//        when(containerRepository.findByContainerId(CONTAINER_1_ID))
//                .thenReturn(CONTAINER_1);
//        when(dockerClient.removeContainerCmd(CONTAINER_1_ID))
//                .thenThrow(NotModifiedException.class);
//
//        Assertions.assertThrows(DockerClientException.class, () -> containerService.start(CONTAINER_1_ID));
//    }
}
