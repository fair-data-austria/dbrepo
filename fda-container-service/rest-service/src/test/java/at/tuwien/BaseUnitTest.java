package at.tuwien;

import at.tuwien.api.container.ContainerBriefDto;
import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerDto;
import at.tuwien.api.container.image.ImageBriefDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.api.container.image.ImageDto;
import at.tuwien.api.container.image.ImageEnvItemDto;
import at.tuwien.api.container.network.IpAddressDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.command.CreateContainerCmdImpl;
import com.github.dockerjava.core.exec.CreateContainerCmdExec;
import org.springframework.test.context.TestPropertySource;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "postgres";
    public final static String IMAGE_1_TAG = "13-alpine";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
    public final static Integer IMAGE_1_PORT = 5432;
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);
    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());
    public final static ImageEnvItemDto[] IMAGE_1_ENV_DTO = List.of(ImageEnvItemDto.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ImageEnvItemDto.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build())
            .toArray(new ImageEnvItemDto[0]);
    public final static List<String> IMAGE_1_ENVIRONMENT = List.of("POSTGRES_USER=postgres",
            "POSTGRES_PASSWORD=postgres");

    public final static String IMAGE_2_REPOSITORY = "redis";
    public final static String IMAGE_2_TAG = "latest";
    public final static String IMAGE_2_HASH = "f877e80bb9ef";
    public final static Integer IMAGE_2_PORT = 6379;
    public final static Long IMAGE_2_SIZE = 24000L;
    public final static Instant IMAGE_2_BUILT = Instant.now().minus(9, DAYS);
    public final static List<ContainerImageEnvironmentItem> IMAGE_2_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());
    public final static ImageEnvItemDto[] IMAGE_2_ENV_DTO = List.of(ImageEnvItemDto.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ImageEnvItemDto.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build())
            .toArray(new ImageEnvItemDto[0]);

    public final static ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(1L)
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .compiled(IMAGE_1_BUILT)
            .size(IMAGE_1_SIZE)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .build();

    public final static ImageDto IMAGE_1_DTO = ImageDto.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .compiled(IMAGE_1_BUILT)
            .size(BigInteger.valueOf(IMAGE_1_SIZE))
            .environment(IMAGE_1_ENV_DTO)
            .defaultPort(IMAGE_1_PORT)
            .build();

    public final static ContainerImage IMAGE_2 = ContainerImage.builder()
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .compiled(IMAGE_2_BUILT)
            .size(IMAGE_2_SIZE)
            .environment(IMAGE_2_ENV)
            .defaultPort(IMAGE_2_PORT)
            .build();

    public final static ImageDto IMAGE_2_DTO = ImageDto.builder()
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .size(BigInteger.valueOf(IMAGE_2_SIZE))
            .compiled(IMAGE_2_BUILT)
            .environment(IMAGE_2_ENV_DTO)
            .defaultPort(IMAGE_2_PORT)
            .build();

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final static String CONTAINER_1_NAME = "fda-userdb-u01";
    public final static String CONTAINER_1_INTERNALNAME = "fda-userdb-u01";
    public final static String CONTAINER_1_DATABASE = "univie";
    public final static String CONTAINER_1_IP = "172.28.0.5";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "0ff1ce";
    public final static ContainerImage CONTAINER_2_IMAGE = IMAGE_2;
    public final static String CONTAINER_2_NAME = "fda-userdb-t01";
    public final static String CONTAINER_2_INTERNALNAME = "fda-userdb-t01";
    public final static String CONTAINER_2_DATABASE = "tuw";
    public final static String CONTAINER_2_IP = "172.28.0.8";
    public final static Instant CONTAINER_2_CREATED = Instant.now().minus(1, HOURS);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final static Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_INTERNALNAME)
            .image(CONTAINER_2_IMAGE)
            .hash(CONTAINER_2_HASH)
            .containerCreated(CONTAINER_2_CREATED)
            .build();

    public final static ContainerDto CONTAINER_1_DTO = ContainerDto.builder()
            .name(CONTAINER_1_NAME)
            .image(IMAGE_1_DTO)
            .hash(CONTAINER_1_HASH)
            .ipAddress(IpAddressDto.builder()
                    .ipv4(CONTAINER_1_IP)
                    .build())
            .created(CONTAINER_1_CREATED)
            .build();

    public final static ContainerBriefDto CONTAINER_1_BRIEF_DTO = ContainerBriefDto.builder()
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .hash(CONTAINER_1_HASH)
            .build();

    public final static ImageBriefDto IMAGE_1_BRIEFDTO = ImageBriefDto.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .build();

    public final static ContainerDto CONTAINER_2_DTO = ContainerDto.builder()
            .name(CONTAINER_2_NAME)
            .image(IMAGE_2_DTO)
            .hash(CONTAINER_2_HASH)
            .ipAddress(IpAddressDto.builder()
                    .ipv4(CONTAINER_2_IP)
                    .build())
            .created(CONTAINER_2_CREATED)
            .build();

    public final static ContainerBriefDto CONTAINER_2_BRIEF_DTO = ContainerBriefDto.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_INTERNALNAME)
            .hash(CONTAINER_2_HASH)
            .build();

    public final static ImageCreateDto IMAGE_1_CREATE_DTO = ImageCreateDto.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .environment(IMAGE_1_ENV_DTO)
            .defaultPort(IMAGE_1_PORT)
            .build();

    public final static ContainerCreateRequestDto CONTAINER_1_CREATE_DTO = ContainerCreateRequestDto.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .name(CONTAINER_1_NAME)
            .build();
}
