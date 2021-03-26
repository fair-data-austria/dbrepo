package at.tuwien.service;

import at.tuwien.api.dto.image.ImageCreateDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public ContainerImage getById(Long containerId) throws ImageNotFoundException {
        final Optional<ContainerImage> image = imageRepository.findById(containerId);
        if (image.isEmpty()) {
            throw new ImageNotFoundException("no image with this id in metadata database");
        }
        return image.get();
    }

    public ContainerImage create(ImageCreateDto createDto) throws ImageNotFoundException {
        final InspectImageResponse response;
        try {
            response = dockerClient.inspectImageCmd(createDto.toCompact())
                    .exec();
        } catch (NotFoundException e) {
            throw new ImageNotFoundException("image not found in library", e);
        }
        final ContainerImage image = imageMapper.inspectImageResponseToContainerImage(response);
        image.setEnvironment(Arrays.asList(createDto.getEnvironment()));
        image.setDefaultPort(createDto.getDefaultPort());
        return imageRepository.save(image);
    }

    public ContainerImage update(Long id) {
        // query new image
        // update in metadata db
        return new ContainerImage();
    }

    public void delete(Long id) throws ImageNotFoundException {
        try {
            imageRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ImageNotFoundException("no image with this id found in metadata database.");
        }
    }

}
