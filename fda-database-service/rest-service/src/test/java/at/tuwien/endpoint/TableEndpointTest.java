package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.repository.ContainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableEndpointTest extends BaseIntegrationTest {

    @MockBean
    private ContainerRepository containerRepository;

    @Test
    public void contextLoads() {
        //
    }
}
