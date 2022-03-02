package at.tuwien.service;

import at.tuwien.entities.container.Container;
import at.tuwien.exception.ContainerNotFoundException;

public interface ContainerService {

    Container find(Long id) throws ContainerNotFoundException;

}
