package at.tuwien.service.impl;

import at.tuwien.entities.container.Container;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.service.ContainerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class ContainerServiceImpl implements ContainerService {

    private final ContainerRepository containerRepository;

    @Autowired
    public ContainerServiceImpl(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    @Override
    public Container find(Long id) throws ContainerNotFoundException {
        final Optional<Container> container = containerRepository.findById(id);
        if (container.isEmpty()) {
            log.error("Failed to find container with id {}", id);
            throw new ContainerNotFoundException("Container not found");
        }
        return container.get();
    }

}
