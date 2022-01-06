package at.tuwien.service.impl;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.mapper.IdentifierMapper;
import at.tuwien.repository.jpa.IdentifierRepository;
import at.tuwien.service.IdentifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IdentifierServiceImpl implements IdentifierService {

    private final IdentifierMapper identifierMapper;
    private final IdentifierRepository identifierRepository;

    @Autowired
    public IdentifierServiceImpl(IdentifierMapper identifierMapper, IdentifierRepository identifierRepository) {
        this.identifierMapper = identifierMapper;
        this.identifierRepository = identifierRepository;
    }

    @Override
    public List<Identifier> findAll() {
        return identifierRepository.findAll();
    }

    @Override
    public Identifier create(IdentifierDto data) {
        final Identifier identifier = identifierMapper.identifierDtoToIdentifier(data);
        final Identifier entity = identifierRepository.save(identifier);
        log.info("Created identifier with id {}", entity.getId());
        log.debug("created identifier {}", entity);
        return entity;
    }

    @Override
    public Identifier find(Long identifierId) throws IdentifierNotFoundException {
        final Optional<Identifier> optional = identifierRepository.findById(identifierId);
        if (optional.isEmpty()) {
            log.error("Unable to find identifier with id {}", identifierId);
            throw new IdentifierNotFoundException("Unable to find identifier");
        }
        return optional.get();
    }

    @Override
    public Identifier update(Long identifierId, IdentifierDto data) throws IdentifierNotFoundException {
        find(identifierId);
        final Identifier identifier = identifierMapper.identifierDtoToIdentifier(data);
        final Identifier entity = identifierRepository.save(identifier);
        log.info("Updated identifier with id {}", identifierId);
        log.debug("updated identifier {}", identifier);
        return entity;
    }

    @Override
    public Identifier publish(Long identifierId) throws IdentifierNotFoundException {
        final Identifier identifier = find(identifierId);
        log.info("Published identifier with id {}", identifierId);
        log.debug("published identifier {}", identifier);
        return identifier;
    }

    @Override
    public void delete(Long identifierId) throws IdentifierNotFoundException {
        final Identifier identifier = find(identifierId);
        identifierRepository.delete(identifier);
        log.info("Deleted identifier with id {}", identifierId);
        log.debug("deleted identifier {}", identifier);
    }

}
