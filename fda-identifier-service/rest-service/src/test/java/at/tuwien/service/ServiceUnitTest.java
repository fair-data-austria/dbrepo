package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.identifier.CreatorDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.CreatorMissingException;
import at.tuwien.exception.IdentifierNotFoundException;
import at.tuwien.repository.jpa.IdentifierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceUnitTest extends BaseUnitTest {

    @Autowired
    private IdentifierService identifierService;

    @MockBean
    private IdentifierRepository identifierRepository;

    @Test
    public void findAll_succeeds() {

        /* mock */
        when(identifierRepository.findAll())
                .thenReturn(List.of(IDENTIFIER_1));

        /* test */
        final List<Identifier> response = identifierService.findAll();
        assertEquals(1, response.size());
        assertEquals(IDENTIFIER_1, response.get(0));
    }

    @Test
    public void findAll_empty_succeeds() {

        /* mock */
        when(identifierRepository.findAll())
                .thenReturn(List.of());

        /* test */
        final List<Identifier> response = identifierService.findAll();
        assertEquals(0, response.size());
    }

    @Test
    public void create_succeeds() {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(IDENTIFIER_1_QUERY_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();
        final Identifier entity = Identifier.builder()
                .qid(IDENTIFIER_1_QUERY_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(List.of(CREATOR_1, CREATOR_2))
                .build();

        /* mock */
        doReturn(IDENTIFIER_1)
                .when(identifierRepository)
                .save(entity);

        /* test */
        final Identifier response = identifierService.create(request);
        assertEquals(IDENTIFIER_1, response);
    }

    @Test
    public void create_nullCreators_fails() {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(IDENTIFIER_1_QUERY_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .build();

        /* test */
        assertThrows(CreatorMissingException.class, () -> {
            identifierService.create(request);
        });
    }

    @Test
    public void create_emptyCreators_fails() {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(IDENTIFIER_1_QUERY_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(new CreatorDto[0])
                .build();

        /* test */
        assertThrows(CreatorMissingException.class, () -> {
            identifierService.create(request);
        });
    }

    @Test
    public void find_succeeds() throws IdentifierNotFoundException {

        /* mock */
        when(identifierRepository.findById(IDENTIFIER_1_ID))
                .thenReturn(Optional.of(IDENTIFIER_1));

        /* test */
        final Identifier response = identifierService.find(IDENTIFIER_1_ID);
        assertEquals(IDENTIFIER_1, response);
    }

    @Test
    public void find_fails() {

        /* mock */
        when(identifierRepository.findById(IDENTIFIER_1_ID))
                .thenReturn(Optional.of(IDENTIFIER_1));

        /* test */
        assertThrows(IdentifierNotFoundException.class, () -> {
            identifierService.find(IDENTIFIER_1_ID);
        });
    }

    @Test
    public void update_succeeds() {

    }

    @Test
    public void update_fails() {

    }

    @Test
    public void publish_succeeds() {

    }

    @Test
    public void publish_fails() {

    }

    @Test
    public void delete_succeeds() {

    }

    @Test
    public void delete_fails() {

    }

}
