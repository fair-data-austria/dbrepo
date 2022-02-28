package at.tuwien.service;

import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.entities.container.Container;
import at.tuwien.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContainerService {
    @Transactional
    Container create(ContainerCreateRequestDto createDto) throws ImageNotFoundException, DockerClientException, ContainerAlreadyExistsException;

    @Transactional
    Container stop(Long containerId) throws ContainerNotFoundException, DockerClientException;

    @Transactional
    void remove(Long containerId) throws ContainerNotFoundException, DockerClientException,
            ContainerStillRunningException;

    @Transactional
    Container find(Long id) throws ContainerNotFoundException;

    @Transactional
    Container inspect(Long id) throws ContainerNotFoundException, DockerClientException, ContainerNotRunningException;

    @Transactional
    List<Container> getAll();

    @Transactional
    Container start(Long containerId) throws ContainerNotFoundException, DockerClientException;
}
