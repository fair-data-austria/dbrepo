package at.tuwien.service.impl;

import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.service.IdentifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IdentifierServiceImpl implements IdentifierService {

    @Override
    public List<IdentifierDto> findAll() {
        return null;
    }

    @Override
    public IdentifierDto create(IdentifierDto data) {
        return null;
    }

    @Override
    public IdentifierDto find(Long identifierId) {
        return null;
    }

    @Override
    public IdentifierDto update(Long identifierId, IdentifierDto data) {
        return null;
    }

    @Override
    public IdentifierDto publish(Long identifierId) {
        return null;
    }

    @Override
    public IdentifierDto delete(Long identifierId) {
        return null;
    }

}
