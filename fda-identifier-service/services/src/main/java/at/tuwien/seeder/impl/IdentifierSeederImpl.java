package at.tuwien.seeder.impl;

import at.tuwien.entities.identifier.Identifier;
import at.tuwien.repository.jpa.IdentifierRepository;
import at.tuwien.seeder.Seeder;
import at.tuwien.service.IdentifierService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IdentifierSeederImpl extends AbstractSeeder implements Seeder {

    private final IdentifierService identifierService;
    private final IdentifierRepository identifierRepository;

    @Autowired
    public IdentifierSeederImpl(IdentifierService identifierService, IdentifierRepository identifierRepository) {
        this.identifierService = identifierService;
        this.identifierRepository = identifierRepository;
    }

    @SneakyThrows
    @Override
    public void seed() {
        if (identifierRepository.findByQid(QUERY_1_ID).isPresent()) {
            log.warn("Already seeded. Skip.");
            return;
        }
        final Identifier identifier1 = identifierService.create(CONTAINER_1_ID, DATABASE_1_ID, IDENTIFIER_1_CREATE_DTO);
        log.info("Saved query id {}", identifier1.getId());
    }

}
