package at.tuwien.service.impl;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.entities.identifier.VisibilityType;
import at.tuwien.exception.IdentifierAlreadyPublishedException;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.exception.IdentifierPublishingNotAllowedException;
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
            log.error("Identifier with id {} not existing", identifierId);
            throw new IdentifierNotFoundException("Unable to find identifier");
        }
        return optional.get();
    }

    @Override
    public Identifier update(Long identifierId, IdentifierDto data) throws IdentifierNotFoundException,
            IdentifierPublishingNotAllowedException {
        final Identifier identifier = find(identifierId);
        final Identifier entity = identifierMapper.identifierDtoToIdentifier(data);
        if (!identifier.getVisibility().equals(entity.getVisibility())) {
            /* in the future we might want to escalate privileges, so use own method for this */
            log.error("Identifier visibility changes not allowed");
            log.debug("visibility changes only supported through the publish() method");
            throw new IdentifierPublishingNotAllowedException("Visibility modification not allowed");
        }
        final Identifier entityUpdated = identifierRepository.save(identifier);
        log.info("Updated identifier with id {}", identifierId);
        log.debug("updated identifier {}", entityUpdated);
        return entityUpdated;
    }

    @Override
    public Identifier publish(Long identifierId, VisibilityTypeDto visibility) throws IdentifierNotFoundException,
            IdentifierAlreadyPublishedException {
        final Identifier identifier = find(identifierId);
        if (identifier.getVisibility().equals(VisibilityType.EVERYONE)
                && !visibility.equals(VisibilityTypeDto.EVERYONE)) {
            /* once published, the identifier cannot be reverted back, it is persistent! */
            log.error("Identifier is already published");
            log.debug("unpublish not supported for identifier {}", identifier);
            throw new IdentifierAlreadyPublishedException("Unpublish not allowed");
        }
        identifier.setVisibility(identifierMapper.visibilityTypeDtoToVisibilityType(visibility));
        final Identifier entity = identifierRepository.save(identifier);
        log.info("Published identifier with id {}", identifierId);
        log.debug("published identifier {}", entity);
        return entity;
    }

    @Override
    public void delete(Long identifierId) throws IdentifierNotFoundException {
        final Identifier identifier = find(identifierId);
        identifierRepository.delete(identifier);
        log.info("Deleted identifier with id {}", identifierId);
        log.debug("deleted identifier {}", identifier);
    }

}
