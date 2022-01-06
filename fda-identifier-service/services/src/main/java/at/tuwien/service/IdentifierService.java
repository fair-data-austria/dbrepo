package at.tuwien.service;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.IdentifierAlreadyPublishedException;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.exception.IdentifierPublishingNotAllowedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IdentifierService {
    List<Identifier> findAll();

    Identifier create(IdentifierDto data);

    Identifier find(Long identifierId) throws IdentifierNotFoundException;

    Identifier update(Long identifierId, IdentifierDto data) throws IdentifierNotFoundException, IdentifierPublishingNotAllowedException;

    Identifier publish(Long identifierId, VisibilityTypeDto visibility) throws IdentifierNotFoundException,
            IdentifierAlreadyPublishedException;

    void delete(Long identifierId) throws IdentifierNotFoundException;
}
