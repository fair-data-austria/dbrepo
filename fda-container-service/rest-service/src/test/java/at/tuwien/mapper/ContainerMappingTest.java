package at.tuwien.mapper;

import at.tuwien.config.ReadyConfig;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContainerMappingTest extends BaseMappingTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Test
    public void inspectContainerResponseToDatabaseContainerMappingTest_succeeds() {
        final InspectContainerResponse response = mockInspectResponse();

        assertNotNull(response, "response must not be null");
        assertEquals(CONTAINER_ID, response.getId());
        assertNotNull(response.getNetworkSettings(), "networkSettings must not be null");
        assertNotNull(response.getNetworkSettings().getNetworks(), "networkSettings.networks must not be null");
        assertNotNull(response.getNetworkSettings().getNetworks().get("fda-userdb"), "networkSettings.networks['fda-userdb'] must not be null");
        assertNotNull(response.getNetworkSettings().getNetworks().get("fda-userdb").getIpAddress(), "networkSettings.networks['fda-userdb'].ipAddress must not be null");
        assertEquals(CONTAINER_NETWORK_IP, response.getNetworkSettings().getNetworks().get("fda-userdb").getIpAddress());
    }

}
