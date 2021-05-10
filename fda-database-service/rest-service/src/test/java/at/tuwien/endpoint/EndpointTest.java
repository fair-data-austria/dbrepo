package at.tuwien.endpoint;

import at.tuwien.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointTest extends BaseIntegrationTest {

    @Test
    public void contextLoads() {
        fail();
    }
}
