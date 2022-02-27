package at.tuwien.service.impl;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.entities.identifier.VisibilityType;
import at.tuwien.exception.*;
import at.tuwien.gateway.QueryServiceGateway;
import at.tuwien.mapper.IdentifierMapper;
import at.tuwien.repository.jpa.IdentifierRepository;
import at.tuwien.service.IdentifierService;
import at.tuwien.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IdentifierServiceImpl implements IdentifierService {

    private final UserService userService;
    private final IdentifierMapper identifierMapper;
    private final QueryServiceGateway queryServiceGateway;
    private final IdentifierRepository identifierRepository;

    @Autowired
    public IdentifierServiceImpl(UserService userService, IdentifierMapper identifierMapper,
                                 QueryServiceGateway queryServiceGateway, IdentifierRepository identifierRepository) {
        this.userService = userService;
        this.identifierMapper = identifierMapper;
        this.queryServiceGateway = queryServiceGateway;
        this.identifierRepository = identifierRepository;
    }

    @Override
    @Transactional
    public List<Identifier> findAll(Long containerId, Long databaseId) {
        return identifierRepository.findAll();
    }

    @Override
    @Transactional
    public Identifier find(Long containerId, Long databaseId, Long queryId) throws IdentifierNotFoundException {
        final Optional<Identifier> identifier = identifierRepository.findByQid(queryId);
        if (identifier.isEmpty()) {
            log.error("Failed to find identifier with query id {}", queryId);
            throw new IdentifierNotFoundException("Failed to find identifier");
        }
        return identifier.get();
    }

    @Override
    @Transactional
    public Identifier create(Long containerId, Long databaseId, IdentifierDto data)
            throws IdentifierPublishingNotAllowedException, QueryNotFoundException,
            RemoteUnavailableException, IdentifierAlreadyExistsException, UserNotFoundException {
        if (!data.getVisibility().equals(VisibilityTypeDto.SELF)) {
            log.error("Identifier must be self visible for creation");
            log.debug("identifier is not self-visible {}", data);
            throw new IdentifierPublishingNotAllowedException("Identifier not self-visible");
        }
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        /* find */
        final Optional<Identifier> optional = identifierRepository.findByQid(data.getQid());
        if (optional.isPresent()) {
            log.error("Identifier already issued for database {} and query id {}", data.getDbid(), data.getQid());
            log.debug("identifier already exists similar to request {}", data);
            throw new IdentifierAlreadyExistsException("Identifier exists");
        }
        final QueryDto query = queryServiceGateway.find(data) /* check if exists */;
        final Identifier identifier = identifierMapper.identifierDtoToIdentifier(data);
        identifier.setCreator(userService.findByUsername(authentication.getName()));
        identifier.setVisibility(identifierMapper.visibilityTypeDtoToVisibilityType(data.getVisibility()));
        /* create in metadata database */
        final Identifier entity = identifierRepository.save(identifier);
        log.info("Created identifier with id {}", entity.getId());
        log.debug("created identifier {}", entity);
        return entity;
    }

    @Override
    @Transactional
    public Identifier find(Long identifierId) throws IdentifierNotFoundException {
        final Optional<Identifier> optional = identifierRepository.findById(identifierId);
        if (optional.isEmpty()) {
            log.error("Identifier with id {} not existing", identifierId);
            throw new IdentifierNotFoundException("Unable to find identifier");
        }
        return optional.get();
    }

    @Override
    @Transactional
    public Identifier update(Long containerId, Long databaseId, Long identifierId, IdentifierDto data)
            throws IdentifierNotFoundException {
        final Identifier entity = find(identifierId);
        final Identifier identifier = identifierMapper.identifierDtoToIdentifier(data);
        identifier.setVisibility(entity.getVisibility()) /* never update visibility */;
        final Identifier entityUpdated = identifierRepository.save(identifier);
        log.info("Updated identifier with id {}", identifierId);
        log.debug("updated identifier {}", entityUpdated);
        return entityUpdated;
    }

    @Override
    @Transactional
    public Identifier publish(Long containerId, Long databaseId, Long identifierId, VisibilityTypeDto visibility)
            throws IdentifierNotFoundException, IdentifierAlreadyPublishedException {
        final Identifier identifier = find(identifierId);
        if (identifier.getVisibility().equals(VisibilityType.EVERYONE)) {
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
    @Transactional
    public void delete(Long containerId, Long databaseId, Long identifierId) throws IdentifierNotFoundException {
        final Identifier identifier = find(identifierId);
        identifierRepository.delete(identifier);
        log.info("Deleted identifier with id {}", identifierId);
        log.debug("deleted identifier {}", identifier);
    }

}
