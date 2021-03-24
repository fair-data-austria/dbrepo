package at.tuwien.mapper;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.NetworkSettings;
import lombok.SneakyThrows;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Map;

public abstract class BaseMappingTest {

    @Configuration
    @ComponentScan(basePackages = {"at.tuwien"})
    public static class BaseMappingContext {
    }

    final String CONTAINER_ID = "deadbeef";
    final String CONTAINER_NETWORK_IP = "154.234.88.15";

    @SneakyThrows
    final InspectContainerResponse mockInspectResponse() {
        final InspectContainerResponse responseC = new InspectContainerResponse();
        final Object response = responseC.getClass().getConstructor().newInstance();
        final Field idField = responseC.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(response, CONTAINER_ID);
        final Field networkSettingsField = responseC.getClass().getDeclaredField("networkSettings");
        networkSettingsField.setAccessible(true);

        // define the network and address
        final ContainerNetwork networkC = new ContainerNetwork();
        final Object network = networkC.getClass().getConstructor().newInstance();
        final Field ipField = networkC.getClass().getDeclaredField("ipAddress");
        ipField.setAccessible(true);
        ipField.set(network, CONTAINER_NETWORK_IP);
        final Map<String, ContainerNetwork> map = Map.of("bridge", (ContainerNetwork) network);

        // add to network settings
        final NetworkSettings settingsC = new NetworkSettings();
        final Object settings = settingsC.getClass().getConstructor().newInstance();
        final Field networksField = settingsC.getClass().getDeclaredField("networks");
        networksField.setAccessible(true);
        networksField.set(settings, map);
        networkSettingsField.set(response, settings);

        return (InspectContainerResponse) response;
    }

}
