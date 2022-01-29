package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableColumnRepository;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.io.File;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;


@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class QueryServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private QueryService queryService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private CommaValueService commaValueService;

    @Autowired
    private TableColumnRepository tableColumnRepository;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    @BeforeAll
    public static void beforeAll() {
        afterAll();

        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();

        /* create container */
        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind);
        final CreateContainerResponse response = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind), Bind.parse("/tmp:/tmp"))
                .exec();
        CONTAINER_1.setHash(response.getId());
        /* create container */
        final String bind3 = new File("./src/test/resources/traffic").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind3);
        final CreateContainerResponse response3 = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_3_INTERNALNAME)
                .withIpv4Address(CONTAINER_3_IP)
                .withHostName(CONTAINER_3_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=traffic")
                .withBinds(Bind.parse(bind3), Bind.parse("/tmp:/tmp"))
                .exec();
        CONTAINER_3.setHash(response3.getId());
    }

    @AfterAll
    public static void afterAll() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    log.info("Delete container {}", Arrays.asList(container.getNames()));
                    try {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    } catch (NotModifiedException e) {
                        // ignore
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                });
        /* remove networks */
        dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .filter(n -> n.getName().startsWith("fda"))
                .forEach(network -> {
                    log.info("Delete network {}", network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @BeforeEach
    @Transactional
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
        TABLE_3.setDatabase(DATABASE_3);
        imageRepository.save(IMAGE_1);
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
        databaseRepository.save(DATABASE_3);
    }

    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            TableMalformedException, InterruptedException, TableNotFoundException, DatabaseConnectionException,
            PaginationException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final QueryResultDto result = queryService.findAll(DATABASE_1_ID, TABLE_1_ID, Instant.now(), null, null);
        assertEquals(3, result.getResult().size());
        assertEquals(BigInteger.valueOf(1L), result.getResult().get(0).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-01"), result.getResult().get(0).get(COLUMN_1_2_NAME));
        assertEquals("Albury", result.getResult().get(0).get(COLUMN_1_3_NAME));
        assertEquals(13.4, result.getResult().get(0).get(COLUMN_1_4_NAME));
        assertEquals(0.6, result.getResult().get(0).get(COLUMN_1_5_NAME));
        assertEquals(BigInteger.valueOf(2L), result.getResult().get(1).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-02"), result.getResult().get(1).get(COLUMN_1_2_NAME));
        assertEquals("Albury", result.getResult().get(1).get(COLUMN_1_3_NAME));
        assertEquals(7.4, result.getResult().get(1).get(COLUMN_1_4_NAME));
        assertEquals(0.0, result.getResult().get(1).get(COLUMN_1_5_NAME));
        assertEquals(BigInteger.valueOf(3L), result.getResult().get(2).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-03"), result.getResult().get(2).get(COLUMN_1_2_NAME));
        assertEquals("Albury", result.getResult().get(2).get(COLUMN_1_3_NAME));
        assertEquals(12.9, result.getResult().get(2).get(COLUMN_1_4_NAME));
        assertEquals(0.0, result.getResult().get(2).get(COLUMN_1_5_NAME));
    }

    @Test
    public void execute_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException, InterruptedException, QueryMalformedException, TableNotFoundException, QueryStoreException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final QueryResultDto response = queryService.execute(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(3, response.getResult().size());
        assertEquals(BigInteger.valueOf(1L), response.getResult().get(0).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-01"), response.getResult().get(0).get(COLUMN_1_2_NAME));
        assertEquals("Albury", response.getResult().get(0).get(COLUMN_1_3_NAME));
        assertEquals(13.4, response.getResult().get(0).get(COLUMN_1_4_NAME));
        assertEquals(0.6, response.getResult().get(0).get(COLUMN_1_5_NAME));
        assertEquals(BigInteger.valueOf(2L), response.getResult().get(1).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-02"), response.getResult().get(1).get(COLUMN_1_2_NAME));
        assertEquals("Albury", response.getResult().get(1).get(COLUMN_1_3_NAME));
        assertEquals(7.4, response.getResult().get(1).get(COLUMN_1_4_NAME));
        assertEquals(0.0, response.getResult().get(1).get(COLUMN_1_5_NAME));
        assertEquals(BigInteger.valueOf(3L), response.getResult().get(2).get(COLUMN_1_1_NAME));
        assertEquals(toInstant("2008-12-03"), response.getResult().get(2).get(COLUMN_1_2_NAME));
        assertEquals("Albury", response.getResult().get(2).get(COLUMN_1_3_NAME));
        assertEquals(12.9, response.getResult().get(2).get(COLUMN_1_4_NAME));
        assertEquals(0.0, response.getResult().get(2).get(COLUMN_1_5_NAME));
    }

    // TODO use own user that has only read-only permissions
    @Test
    @Disabled
    public void execute_modifyData_fails() throws DatabaseNotFoundException, ImageNotSupportedException, InterruptedException, QueryMalformedException, TableNotFoundException, QueryStoreException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement("DELETE FROM `weather_aus`;")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        final QueryResultDto response = queryService.execute(DATABASE_1_ID, TABLE_1_ID, request);
        assertNotNull(response.getResult());
        assertEquals(3, response.getResult().size());
    }

    @Test
    public void execute_tableNotExists_fails() throws InterruptedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            queryService.execute(DATABASE_1_ID, 9999L, request);
        });
    }

    @Test
    public void execute_databaseNotExists_fails() throws InterruptedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            queryService.execute(9999L, TABLE_1_ID, request);
        });
    }

    @Test
    @Disabled
    public void execute_tableNotFound_fails() throws InterruptedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(PersistenceException.class, () -> {
            queryService.execute(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void execute_columnNotFound_fails() throws InterruptedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement("SELECT `local` FROM `weather_aus`")
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(PersistenceException.class, () -> {
            queryService.execute(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void execute_statementNull_fails() throws InterruptedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(null)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);

        /* test */
        assertThrows(QueryMalformedException.class, () -> {
            queryService.execute(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insert_succeeds() throws InterruptedException, TableNotFoundException, DatabaseNotFoundException,
            TableMalformedException, ImageNotSupportedException, SQLException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_3);

        /* test */
        final Integer rows = queryService.insert(DATABASE_3_ID, TABLE_3_ID, "/tmp/csv_12.csv");
        assertEquals(9999, rows);
        final List<List<String>> response = MariaDbConfig.select(TABLE_3, 1);
        assertEquals("1", response.get(0).get(0));
        assertEquals("2", response.get(0).get(1));
        assertEquals("1", response.get(0).get(2));
        assertEquals("2017-01-15", response.get(0).get(3));
        assertEquals("2076", response.get(0).get(4));
        assertEquals("6", response.get(0).get(5));
        assertEquals("1", response.get(0).get(6));
        assertEquals("6030", response.get(0).get(7));
        assertEquals("0", response.get(0).get(8));
        assertEquals("DEP4", response.get(0).get(9));
        assertEquals("2017-01-15", response.get(0).get(10));
        assertEquals("17580", response.get(0).get(11));
        assertEquals("17562", response.get(0).get(12));
        assertEquals("17580", response.get(0).get(13));
        assertEquals("17562", response.get(0).get(14));
        assertEquals("2", response.get(0).get(15));
        assertEquals("1357", response.get(0).get(16));
        assertEquals("1", response.get(0).get(17));
        assertEquals("KALK", response.get(0).get(18));
        assertEquals("2017-01-15", response.get(0).get(19));
        assertEquals("17622", response.get(0).get(20));
        assertEquals("17647", response.get(0).get(21));
        assertEquals("17622", response.get(0).get(22));
        assertEquals("17664", response.get(0).get(23));
        assertEquals("8538", response.get(0).get(24));
        assertEquals("41253", response.get(0).get(25));
        assertEquals("15", response.get(0).get(26));
        assertEquals("2", response.get(0).get(27));
        assertEquals("15", response.get(0).get(28));
        assertEquals("DEP4 - KALK", response.get(0).get(29));
        assertEquals("135780", response.get(0).get(30));
        assertEquals("2251", response.get(0).get(31));
        assertEquals("1906", response.get(0).get(32));
        assertEquals("12462", response.get(0).get(33));
        assertEquals("10563", response.get(0).get(34));

    }

    @Test
    public void insert_large_succeeds() throws InterruptedException, TableNotFoundException, DatabaseNotFoundException,
            TableMalformedException, ImageNotSupportedException, SQLException {

        /* mock */
        DockerConfig.startContainer(CONTAINER_3);

        /* test */
        final Integer rows = queryService.insert(DATABASE_3_ID, TABLE_3_ID, "/tmp/csv_13.csv");
        assertEquals(1397856, rows);
        final List<List<String>> response = MariaDbConfig.select(TABLE_3, 1);
        assertEquals("1", response.get(0).get(0));
        assertEquals("2", response.get(0).get(1));
        assertEquals("1", response.get(0).get(2));
        assertEquals("2017-01-15", response.get(0).get(3));
        assertEquals("2076", response.get(0).get(4));
        assertEquals("6", response.get(0).get(5));
        assertEquals("1", response.get(0).get(6));
        assertEquals("6030", response.get(0).get(7));
        assertEquals("0", response.get(0).get(8));
        assertEquals("DEP4", response.get(0).get(9));
        assertEquals("2017-01-15", response.get(0).get(10));
        assertEquals("17580", response.get(0).get(11));
        assertEquals("17562", response.get(0).get(12));
        assertEquals("17580", response.get(0).get(13));
        assertEquals("17562", response.get(0).get(14));
        assertEquals("2", response.get(0).get(15));
        assertEquals("1357", response.get(0).get(16));
        assertEquals("1", response.get(0).get(17));
        assertEquals("KALK", response.get(0).get(18));
        assertEquals("2017-01-15", response.get(0).get(19));
        assertEquals("17622", response.get(0).get(20));
        assertEquals("17647", response.get(0).get(21));
        assertEquals("17622", response.get(0).get(22));
        assertEquals("17664", response.get(0).get(23));
        assertEquals("8538", response.get(0).get(24));
        assertEquals("41253", response.get(0).get(25));
        assertEquals("15", response.get(0).get(26));
        assertEquals("2", response.get(0).get(27));
        assertEquals("15", response.get(0).get(28));
        assertEquals("DEP4 - KALK", response.get(0).get(29));
        assertEquals("135780", response.get(0).get(30));
        assertEquals("2251", response.get(0).get(31));
        assertEquals("1906", response.get(0).get(32));
        assertEquals("12462", response.get(0).get(33));
        assertEquals("10563", response.get(0).get(34));
    }

    @Test
    public void insert_sensor_succeeds() throws InterruptedException, TableNotFoundException, DatabaseNotFoundException,
            TableMalformedException, ImageNotSupportedException, SQLException {
        final TableCsvDto request = TableCsvDto.builder()
                .header(List.of("linie","richtung","betriebsdatum","fahrzeug","kurs","seq_von","halt_diva_von","halt_punkt_diva_von","halt_kurz_von1","datum_von","soll_an_von","ist_an_von","soll_ab_von","seq_nach","halt_diva_nach","halt_punkt_diva_nach","halt_kurz_nach1","datum_nach","soll_an_nach","ist_an_nach1","soll_ab_nach","ist_ab_nach","fahrt_id","fahrweg_id","fw_no","fw_typ","fw_kurz","fw_lang","umlauf_von","halt_id_von","halt_id_nach","halt_punkt_id_von","halt_punkt_id_nach"))
                .data(List.of(List.of("2","1","15.01.17","2076","6","1","6030","0","DEP4","15.01.17","17580","17562","17580","17562","2","1357","1","KALK","15.01.17","17622","17647","17622","17664","8538","41253","15","2","15","DEP4 - KALK","135780","2251","1906","12462","10563")))
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_3);

        /* test */
        final Integer rows = queryService.insert(DATABASE_3_ID, TABLE_3_ID, request);
        assertEquals(1, rows);
        final List<List<String>> response = MariaDbConfig.select(TABLE_3, 1);
        assertEquals("1", response.get(0).get(0));
        assertEquals("2", response.get(0).get(1));
        assertEquals("1", response.get(0).get(2));
        assertEquals("2017-01-15", response.get(0).get(3));
        assertEquals("2076", response.get(0).get(4));
        assertEquals("6", response.get(0).get(5));
        assertEquals("1", response.get(0).get(6));
        assertEquals("6030", response.get(0).get(7));
        assertEquals("0", response.get(0).get(8));
        assertEquals("DEP4", response.get(0).get(9));
        assertEquals("2017-01-15", response.get(0).get(10));
        assertEquals("17580", response.get(0).get(11));
        assertEquals("17562", response.get(0).get(12));
        assertEquals("17580", response.get(0).get(13));
        assertEquals("17562", response.get(0).get(14));
        assertEquals("2", response.get(0).get(15));
        assertEquals("1357", response.get(0).get(16));
        assertEquals("1", response.get(0).get(17));
        assertEquals("KALK", response.get(0).get(18));
        assertEquals("2017-01-15", response.get(0).get(19));
        assertEquals("17622", response.get(0).get(20));
        assertEquals("17647", response.get(0).get(21));
        assertEquals("17622", response.get(0).get(22));
        assertEquals("17664", response.get(0).get(23));
        assertEquals("8538", response.get(0).get(24));
        assertEquals("41253", response.get(0).get(25));
        assertEquals("15", response.get(0).get(26));
        assertEquals("2", response.get(0).get(27));
        assertEquals("15", response.get(0).get(28));
        assertEquals("DEP4 - KALK", response.get(0).get(29));
        assertEquals("135780", response.get(0).get(30));
        assertEquals("2251", response.get(0).get(31));
        assertEquals("1906", response.get(0).get(32));
        assertEquals("12462", response.get(0).get(33));
        assertEquals("10563", response.get(0).get(34));
    }

    @SneakyThrows
    private static Instant toInstant(String str) {
        final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive() /* case insensitive to parse JAN and FEB */
                .appendPattern("yyyy-MM-dd")
                .toFormatter(Locale.ENGLISH);
        final LocalDate date = LocalDate.parse(str, formatter);
        return date.atStartOfDay(ZoneId.of("UTC"))
                .toInstant();
    }

}
