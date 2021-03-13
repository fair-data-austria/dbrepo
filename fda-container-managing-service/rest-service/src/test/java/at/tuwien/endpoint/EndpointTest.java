package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.service.ContainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EndpointTest extends BaseIntegrationTest {

    @MockBean
    private ContainerService containerService;

    @Test
    public void listAllDatabases_success() {
        //
    }
}
