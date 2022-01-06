package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.identifier.CreatorDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.exception.IdentifierPublishingNotAllowedException;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.RemoteUnavailableException;
import at.tuwien.repository.jpa.IdentifierRepository;
import at.tuwien.service.impl.IdentifierServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IdentifierServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private IdentifierServiceImpl identifierService;

    @Autowired
    private IdentifierRepository identifierRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        identifierRepository.save(IDENTIFIER_1);
    }

    @Test
    public void findAll_succeeds() {

        /* mock */
        identifierRepository.save(Identifier.builder()
                .id(2L)
                .qid(2L)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .visibility(IDENTIFIER_1_VISIBILITY)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(List.of(CREATOR_1, CREATOR_2))
                .deleted(Instant.now().minus(4, ChronoUnit.MINUTES))
                .build());

        /* test */
        final List<Identifier> response = identifierService.findAll();
        assertEquals(1, response.size());
        assertEquals(IDENTIFIER_1, response.get(0));
    }

    @Test
    public void create_succeeds() throws IdentifierPublishingNotAllowedException, QueryNotFoundException,
            RemoteUnavailableException {

        /* test */
        final Identifier response = identifierService.create(IDENTIFIER_2_DTO_REQUEST);
        assertEquals(IDENTIFIER_2, response);
    }

    @Test
    public void create_queryNotExists_fails() {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(9999L)
                .description(IDENTIFIER_2_DESCRIPTION)
                .title(IDENTIFIER_2_TITLE)
                .doi(IDENTIFIER_2_DOI)
                .visibility(IDENTIFIER_2_VISIBILITY_DTO)
                .created(IDENTIFIER_2_CREATED)
                .lastModified(IDENTIFIER_2_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            identifierService.create(request);
        });
    }

    @Test
    public void create_queryServiceUnavailable_fails() {

        /* test */
        assertThrows(RemoteUnavailableException.class, () -> {
            identifierService.create(IDENTIFIER_2_DTO_REQUEST);
        });
    }

    @Test
    public void find_succeeds() {
        fail();
    }

    @Test
    public void find_fails() {
        fail();
    }

    @Test
    public void update_fails() {
        fail();
    }

    @Test
    public void update_succeeds() throws IdentifierNotFoundException, IdentifierPublishingNotAllowedException {

        /* test */
        final Identifier response = identifierService.update(IDENTIFIER_1_ID, IDENTIFIER_1_DTO);
        assertEquals(response, IDENTIFIER_1);
    }

    @Test
    public void update_visibilityTrusted_fails() {
        final IdentifierDto request = IdentifierDto.builder()
                .id(IDENTIFIER_1_ID)
                .qid(IDENTIFIER_1_QUERY_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .visibility(VisibilityTypeDto.TRUSTED)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();

        /* test */
        assertThrows(IdentifierPublishingNotAllowedException.class, () -> {
            identifierService.update(IDENTIFIER_1_ID, request);
        });
    }

    @Test
    public void publish_succeeds() {
        fail();
    }

    @Test
    public void publish_fails() {
        fail();
    }

    @Test
    public void delete_succeeds() {
        fail();
    }

    @Test
    public void delete_fails() {
        fail();
    }

}
