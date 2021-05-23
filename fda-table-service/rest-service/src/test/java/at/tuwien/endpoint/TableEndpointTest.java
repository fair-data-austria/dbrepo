package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.service.TableService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableEndpointTest extends BaseIntegrationTest {

    @MockBean
    private TableService tableService;

    @Autowired
    private TableEndpoint tableEndpoint;

    @Test
    public void findAll_succeeds() {

    }

    @Test
    public void create_succeeds() {

    }

    @Test
    public void create_notFound_fails() {

    }

    @Test
    public void create_notPostgres_fails() {

    }

    @Test
    public void findById_succeeds() {

    }

    @Test
    public void findById_notFound_fails() {

    }

    @Test
    public void findById_notPostgres_fails() {

    }

    @Test
    public void delete_notFound_fails() {

    }

    @Disabled
    @Test
    public void insert() {

    }

    @Disabled
    @Test
    public void showData() {

    }
}
