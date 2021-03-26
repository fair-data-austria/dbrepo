package at.tuwien.service;

import at.tuwien.api.dto.image.ImageCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
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

    public ContainerImage create(ImageCreateDto createDto) {
        // query docker
        return new ContainerImage();
    }

    public ContainerImage update(Long id) {
        // query new image
        // update in metadata db
        return new ContainerImage();
    }

    public void delete(Long id) throws ImageNotFoundException {
        try {
            imageRepository.deleteById(id);
        } catch(EntityNotFoundException e) {
            throw new ImageNotFoundException("no image with this id found in metadata database.");
        }
    }

}
