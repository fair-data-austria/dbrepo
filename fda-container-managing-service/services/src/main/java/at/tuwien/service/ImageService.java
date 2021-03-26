package at.tuwien.service;

import at.tuwien.api.dto.image.ImageCreateDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ImageService {

    private final DockerClient dockerClient;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageService(DockerClient dockerClient, ImageRepository imageRepository, ImageMapper imageMapper) {
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    public List<ContainerImage> getAll() {
        return imageRepository.findAll();
    }

    public ContainerImage getById(Long imageId) throws ImageNotFoundException {
        final Optional<ContainerImage> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            log.error("image id {} not found in library", imageId);
            throw new ImageNotFoundException("no image with this id in metadata database");
        }
        return image.get();
    }

    public ContainerImage create(ImageCreateDto createDto) throws ImageNotFoundException {
        pull(createDto.getRepository(), createDto.getTag());
        final ContainerImage image = inspect(createDto.getRepository(), createDto.getTag());
        image.setEnvironment(Arrays.asList(createDto.getEnvironment()));
        image.setDefaultPort(createDto.getDefaultPort());
        return imageRepository.save(image);
    }

    public ContainerImage update(Long imageId) throws ImageNotFoundException {
        final ContainerImage imageOld = getById(imageId);
        /* pull changes */
        pull(imageOld.getRepository(), imageOld.getTag());
        /* get new infos */
        final ContainerImage image = inspect(imageOld.getRepository(), imageOld.getTag());
        image.setId(imageOld.getId());
        /* update metadata db */
        return imageRepository.save(image);
    }

    public void delete(Long databaseId) throws ImageNotFoundException {
        try {
            imageRepository.deleteById(databaseId);
        } catch (EntityNotFoundException e) {
            log.error("image id {} not found in metadata database", databaseId);
            throw new ImageNotFoundException("no image with this id found in metadata database.");
        }
    }

    /** HELPER FUNCTIONS */
    private ContainerImage inspect(String repository, String tag) throws ImageNotFoundException {
        final InspectImageResponse response;
        try {
            response = dockerClient.inspectImageCmd(repository + ":" + tag)
                    .exec();
        } catch (NotFoundException e) {
            log.error("image {}:{} not found in library", repository, tag);
            throw new ImageNotFoundException("image not found in library", e);
        }
        return imageMapper.inspectImageResponseToContainerImage(response);
    }

    private void pull(String repository, String tag) throws ImageNotFoundException {
        final ResultCallback.Adapter<PullResponseItem> response;
        try {
            response = dockerClient.pullImageCmd("library")
                    .withRepository(repository)
                    .withTag(tag)
                    .start();
            response.awaitCompletion();
        } catch (NotFoundException | InterruptedException e) {
            log.error("image {}:{} not found in library", repository, tag);
            throw new ImageNotFoundException("image not found in library", e);
        }
    }

}
