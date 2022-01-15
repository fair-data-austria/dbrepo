package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.container.image.ImageBriefDto;
import at.tuwien.api.container.image.ImageChangeDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.api.container.image.ImageDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.ImageEndpoint;
import at.tuwien.exception.*;
import at.tuwien.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private ImageServiceImpl imageService;

    @Autowired
    private ImageEndpoint imageEndpoint;

    @Test
    public void findAll_succeeds() {
        when(imageService.getAll())
                .thenReturn(List.of(IMAGE_1));

        /* test */
        final ResponseEntity<List<ImageBriefDto>> response = imageEndpoint.findAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws ImageNotFoundException, DockerClientException, ImageAlreadyExistsException {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .build();
        when(imageService.create(request))
                .thenReturn(IMAGE_1);

        /* test */
        final ResponseEntity<ImageDto> response = imageEndpoint.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(IMAGE_1_REPOSITORY, Objects.requireNonNull(response.getBody()).getRepository());
        assertEquals(IMAGE_1_TAG, Objects.requireNonNull(response.getBody()).getTag());
    }

    @Test
    public void create_duplicate_fails() throws ImageNotFoundException, DockerClientException, ImageAlreadyExistsException {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .build();
        given(imageService.create(request))
                .willAnswer(invocation -> {
                    throw new ImageAlreadyExistsException("duplicate");
                });

        /* test */
        assertThrows(ImageAlreadyExistsException.class, () -> {
            imageEndpoint.create(request);
        });
    }

    @Test
    public void create_notExists_fails() throws ImageNotFoundException, DockerClientException, ImageAlreadyExistsException {
        final ImageCreateDto request = ImageCreateDto.builder()
                .repository(IMAGE_1_REPOSITORY)
                .tag(IMAGE_1_TAG)
                .defaultPort(IMAGE_1_PORT)
                .environment(IMAGE_1_ENV_DTO)
                .build();
        given(imageService.create(request))
                .willAnswer(invocation -> {
                    throw new ImageNotFoundException("not existing in docker hub");
                });

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageEndpoint.create(request);
        });
    }

    @Test
    public void findById_succeeds() throws ImageNotFoundException {
        when(imageService.find(IMAGE_1_ID))
                .thenReturn(IMAGE_1);

        /* test */
        final ResponseEntity<ImageDto> response = imageEndpoint.findById(IMAGE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void findById_notFound_fails() throws ImageNotFoundException {
        given(imageService.find(IMAGE_1_ID))
                .willAnswer(invocation -> {
                    throw new ImageNotFoundException("not existing in docker hub");
                });

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageEndpoint.findById(IMAGE_1_ID);
        });
    }

    @Test
    public void delete_success() throws ImageNotFoundException, PersistenceException {
        doNothing()
                .when(imageService)
                .delete(IMAGE_1_ID);

        /* test */
        final ResponseEntity<?> response = imageEndpoint.delete(IMAGE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void delete_fails() throws ImageNotFoundException, PersistenceException {
        doThrow(new ImageNotFoundException("not found"))
                .when(imageService)
                .delete(IMAGE_1_ID);

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageEndpoint.delete(IMAGE_1_ID);
        });
    }

    @Test
    public void update_succeeds() throws ImageNotFoundException, DockerClientException {
        final ImageChangeDto request = ImageChangeDto.builder()
                .defaultPort(1111)
                .build();

        /* test */
        final ResponseEntity<ImageDto> response = imageEndpoint.update(IMAGE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void update_notFound_fails() throws ImageNotFoundException, DockerClientException {
        final ImageChangeDto request = ImageChangeDto.builder()
                .defaultPort(1111)
                .environment(IMAGE_1_ENV_DTO)
                .build();
        given(imageService.update(IMAGE_1_ID, request))
                .willAnswer(invocation -> {
                    throw new ImageNotFoundException("not existing in docker hub");
                });

        /* test */
        assertThrows(ImageNotFoundException.class, () -> {
            imageEndpoint.update(IMAGE_1_ID, request);
        });
    }
}
