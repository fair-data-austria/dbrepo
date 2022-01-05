package at.tuwien.service;

import at.tuwien.api.identifier.IdentifierDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IdentifierService {
    List<IdentifierDto> findAll();

    IdentifierDto create(IdentifierDto data);

    IdentifierDto find(Long identifierId);

    IdentifierDto update(Long identifierId, IdentifierDto data);

    IdentifierDto publish(Long identifierId);

    IdentifierDto delete(Long identifierId);
}
