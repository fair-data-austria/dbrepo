package at.tuwien;

import at.tuwien.entity.Architecture;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.DatabaseContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseIntegrationTest {

    public final String IMAGE_1_REPOSITORY = "postgres";
    public final String IMAGE_1_TAG = "13-alpine";
    public final String IMAGE_1_HASH = "83b40f2726e5";
    public final Integer IMAGE_1_PORT = 5432;
    public final BigInteger IMAGE_1_SIZE = new BigInteger("160000000");
    public final Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);
    public final List<String> IMAGE_1_ENV = Arrays.asList("POSTGRES_USER=postgres", "POSTGRES_PASSWORD=postgres");

    public final String IMAGE_2_REPOSITORY = "redis";
    public final String IMAGE_2_TAG = "latest";
    public final String IMAGE_2_HASH = "f877e80bb9ef";
    public final Integer IMAGE_2_PORT = 6379;
    public final BigInteger IMAGE_2_SIZE = new BigInteger("105000000");
    public final Instant IMAGE_2_BUILT = Instant.now().minus(9, DAYS);
    public final List<String> IMAGE_2_ENV = Arrays.asList();

    public final ContainerImage IMAGE_1 = ContainerImage.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .size(IMAGE_1_SIZE)
            .built(IMAGE_1_BUILT)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .architecture(Architecture.LINUX_AMD64)
            .build();

    public final ContainerImage IMAGE_2 = ContainerImage.builder()
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .size(IMAGE_2_SIZE)
            .built(IMAGE_2_BUILT)
            .environment(IMAGE_2_ENV)
            .defaultPort(IMAGE_2_PORT)
            .architecture(Architecture.LINUX_AMD64)
            .build();

    public final String CONTAINER_1_ID = "deadbeef";
    public final ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final String CONTAINER_1_NAME = "u01";
    public final String CONTAINER_1_DATABASE = "univie";
    public final String CONTAINER_1_IP = "231.145.98.83";
    public final Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final String CONTAINER_2_ID = "0ff1ce";
    public final ContainerImage CONTAINER_2_IMAGE = IMAGE_2;
    public final String CONTAINER_2_NAME = "t01";
    public final String CONTAINER_2_DATABASE = "tuw";
    public final String CONTAINER_2_IP = "233.145.99.83";
    public final Instant CONTAINER_2_CREATED = Instant.now().minus(1, HOURS);

    public final DatabaseContainer CONTAINER_1 = DatabaseContainer.builder()
            .containerId(CONTAINER_1_ID)
            .databaseName(CONTAINER_1_DATABASE)
            .ipAddress(CONTAINER_1_IP)
            .image(CONTAINER_1_IMAGE)
            .containerId(CONTAINER_1_ID)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final DatabaseContainer CONTAINER_2 = DatabaseContainer.builder()
            .containerId(CONTAINER_2_ID)
            .databaseName(CONTAINER_2_DATABASE)
            .ipAddress(CONTAINER_2_IP)
            .image(CONTAINER_2_IMAGE)
            .containerId(CONTAINER_2_ID)
            .containerCreated(CONTAINER_2_CREATED)
            .build();

    public final InspectContainerResponse inspectContainerResponse(DatabaseContainer container, Boolean running) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        final InspectContainerResponse.ContainerState stateC = new InspectContainerResponse().new ContainerState();
        final Object state = stateC.getClass().getConstructor().newInstance();
        final Field runningField = stateC.getClass().getDeclaredField("running");
        runningField.setAccessible(true);
        runningField.setBoolean(state, running);

        final InspectContainerResponse inspectC = new InspectContainerResponse();
        final Object response = inspectC.getClass().getConstructor().newInstance();
        final Field stateField = inspectC.getClass().getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(response, state);

        return (InspectContainerResponse) response;
    }

}
