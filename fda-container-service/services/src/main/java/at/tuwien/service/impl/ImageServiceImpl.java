package at.tuwien.service.impl;

import at.tuwien.api.container.image.ImageChangeDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageAlreadyExistsException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.exception.PersistenceException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.service.ImageService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final DockerClient dockerClient;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageServiceImpl(DockerClient dockerClient, ImageRepository imageRepository, ImageMapper imageMapper) {
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.imageMapper = imageMapper;
    }

    @Override
    @Transactional
    public List<ContainerImage> getAll() {
        return imageRepository.findAll();
    }

    @Override
    @Transactional
    public ContainerImage find(Long imageId) throws ImageNotFoundException {
        final Optional<ContainerImage> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            log.error("image id {} not found in library", imageId);
            throw new ImageNotFoundException("no image with this id in metadata database");
        }
        return image.get();
    }

    @Override
    @Transactional
    public ContainerImage create(ImageCreateDto createDto) throws ImageNotFoundException, ImageAlreadyExistsException,
            DockerClientException {
        pull(createDto.getRepository(), createDto.getTag());
        final ContainerImage image = inspect(createDto.getRepository(), createDto.getTag());
        if (imageRepository.findByRepositoryAndTag(createDto.getRepository(), createDto.getTag()).isPresent()) {
            log.error("image already exists: {}", createDto);
            throw new ImageAlreadyExistsException("image already exists");
        }
        image.setEnvironment(imageMapper.imageEnvironmentItemDtoToEnvironmentItemList(createDto.getEnvironment()));
        image.setDefaultPort(createDto.getDefaultPort());
        image.setDialect(createDto.getDialect());
        image.setLogo(createDto.getLogo());
        image.setDriverClass(createDto.getDriverClass());
        image.setJdbcMethod(createDto.getJdbcMethod());
        final ContainerImage out;
        try {
            out = imageRepository.save(image);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            log.error("image already exists: {}", createDto);
            throw new ImageAlreadyExistsException("image already exists");
        }
        log.info("Created image {}", out.getId());
        log.debug("created image {}", out);
        return out;
    }

    @Override
    @Transactional
    public ContainerImage update(Long imageId, ImageChangeDto changeDto) throws ImageNotFoundException {
        final ContainerImage image = find(imageId);
        /* pull changes */
        pull(image.getRepository(), image.getTag());
        /* get new infos */
        final ContainerImage dockerImage = inspect(image.getRepository(), image.getTag());
        if (!changeDto.getDefaultPort().equals(image.getDefaultPort())) {
            image.setDefaultPort(changeDto.getDefaultPort());
            log.debug("port changed for image {}", changeDto);
        }
        if (!List.of(changeDto.getEnvironment()).equals(image.getEnvironment())) {
            image.setEnvironment(imageMapper.imageEnvironmentItemDtoToEnvironmentItemList(changeDto.getEnvironment()));
            log.debug("env changed for image {}", changeDto);
        }
        image.setCompiled(dockerImage.getCompiled());
        image.setHash(dockerImage.getHash());
        image.setSize(dockerImage.getSize());
        image.setDialect(changeDto.getDialect());
        image.setDriverClass(changeDto.getDriverClass());
        image.setJdbcMethod(changeDto.getJdbcMethod());
        /* update metadata db */
        final ContainerImage out = imageRepository.save(image);
        log.info("Updated image {}", out.getId());
        log.debug("updated image {}", out);
        return out;
    }

    @Override
    @Transactional
    public void delete(Long id) throws ImageNotFoundException, PersistenceException {
        try {
            imageRepository.deleteById(id);
        } catch (EntityNotFoundException | EmptyResultDataAccessException | ConstraintViolationException e) {
            log.warn("image id {} not found in metadata database", id);
            throw new ImageNotFoundException("Container image not found", e);
        }
        log.info("Deleted image {}", id);
    }

    /**
     * Inspects a container image by given repository and tag.
     *
     * @param repository The repository.
     * @param tag        The tag.
     * @return The container image if successful.
     * @throws ImageNotFoundException The image was not found.
     */
    public ContainerImage inspect(String repository, String tag) throws ImageNotFoundException {
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

    /**
     * Pulls a container image by given repository and tag.
     *
     * @param repository The repository.
     * @param tag        The tag.
     * @throws ImageNotFoundException The image was not found.
     */
    public void pull(String repository, String tag) throws ImageNotFoundException {
        final ResultCallback.Adapter<PullResponseItem> response;
        try {
            response = dockerClient.pullImageCmd(repository)
                    .withTag(tag)
                    .start();
            final Instant now = Instant.now();
            response.awaitCompletion();
            log.debug("pulled image in {} seconds", Duration.between(now, Instant.now()).getSeconds());
        } catch (NotFoundException | InterruptedException | InternalServerErrorException e) {
            log.warn("image {}:{} not found in library", repository, tag);
            throw new ImageNotFoundException("image not found in library", e);
        }
    }

}
