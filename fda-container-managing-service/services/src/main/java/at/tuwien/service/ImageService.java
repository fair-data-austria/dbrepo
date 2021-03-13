package at.tuwien.service;

import at.tuwien.entity.ContainerImage;
import at.tuwien.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
