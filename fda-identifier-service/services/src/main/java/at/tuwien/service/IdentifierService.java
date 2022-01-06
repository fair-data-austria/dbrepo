package at.tuwien.service;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.IdentifierNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IdentifierService {
    List<Identifier> findAll();

    Identifier create(IdentifierDto data);

    Identifier find(Long identifierId) throws IdentifierNotFoundException;

    Identifier update(Long identifierId, IdentifierDto data) throws IdentifierNotFoundException;

    Identifier publish(Long identifierId) throws IdentifierNotFoundException;

    void delete(Long identifierId) throws IdentifierNotFoundException;
}
