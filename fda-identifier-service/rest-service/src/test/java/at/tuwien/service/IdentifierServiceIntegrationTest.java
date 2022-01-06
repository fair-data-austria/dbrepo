package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.repository.jpa.IdentifierRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IdentifierServiceIntegrationTest extends BaseUnitTest {

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
    public void findAll_fails() {
        fail();
    }

    @Test
    public void create_succeeds() {
        fail();
    }

    @Test
    public void create_exists_fails() {
        fail();
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
    public void update_succeeds() {
        fail();
    }

    @Test
    public void update_fails() {
        fail();
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
