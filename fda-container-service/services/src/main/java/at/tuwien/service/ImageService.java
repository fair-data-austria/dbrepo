package at.tuwien.service;

import at.tuwien.api.container.image.ImageChangeDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageAlreadyExistsException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.exception.PersistenceException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ImageService {

    /**
     * Finds all container images in the metadata database
     *
     * @return A list of container images
     */
    List<ContainerImage> getAll();

    /**
     * Finds a specific container image by given id
     *
     * @param imageId The id.
     * @return The image, if found.
     * @throws ImageNotFoundException The image was not found
     */
    ContainerImage find(Long imageId) throws ImageNotFoundException;

    /**
     * Creates a new container image in the metadata database.
     *
     * @param createDto The new image.
     * @return The created container image, if successful.
     * @throws ImageNotFoundException      The image was not found in the remote repository (e.g. Docker Registry)
     * @throws ImageAlreadyExistsException The image already exists.
     * @throws DockerClientException       The docker client encountered a problem.
     */
    ContainerImage create(ImageCreateDto createDto) throws ImageNotFoundException, ImageAlreadyExistsException, DockerClientException;

    /**
     * Updates a container image in the metadata database by given id.
     *
     * @param imageId   The id.
     * @param changeDto The update request.
     * @return The updated container image, if successful.
     * @throws ImageNotFoundException The image was not found in the metadata database.
     * @throws DockerClientException  The docker client encountered a problem.
     */
    ContainerImage update(Long imageId, ImageChangeDto changeDto) throws ImageNotFoundException, DockerClientException;

    /**
     * Deletes a container image in the metadata database by given id.
     *
     * @param id The id.
     * @throws ImageNotFoundException The image was not found.
     * @throws PersistenceException   The database returned an error.
     */
    void delete(Long id) throws ImageNotFoundException, PersistenceException;
}
