package at.tuwien.service;

import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface ContainerService {

    /**
     * Creates a container with given information.
     *
     * @param createDto The information.
     * @return The created container as stored in the metadata database.
     * @throws ImageNotFoundException The image for the container was not found in the metadata database.
     * @throws DockerClientException  The image for the container was not found on the server.
     * @throws UserNotFoundException  The user was not found.
     */
    Container create(ContainerCreateRequestDto createDto) throws ImageNotFoundException,
            DockerClientException, UserNotFoundException;

    Container stop(Long containerId) throws ContainerNotFoundException, DockerClientException;

    void remove(Long containerId) throws ContainerNotFoundException, DockerClientException,
            ContainerStillRunningException;

    Container find(Long id) throws ContainerNotFoundException;

    Container inspect(Long id) throws ContainerNotFoundException, DockerClientException, ContainerNotRunningException;

    List<Container> getAll();

    Container start(Long containerId) throws ContainerNotFoundException, DockerClientException;
}
