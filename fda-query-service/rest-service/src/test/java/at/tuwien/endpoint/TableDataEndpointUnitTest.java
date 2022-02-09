package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ImportDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.service.CommaValueService;
import at.tuwien.service.impl.QueryServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableDataEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private TableDataEndpoint dataEndpoint;

    @MockBean
    private QueryServiceImpl queryService;

    @MockBean
    private CommaValueService commaValueService;

    @Test
    public void insert_succeeds() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException, ContainerNotFoundException {
        final ImportDto request = ImportDto.builder()
                .location("test:csv/csv_01.csv")
                .build();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.importCsv(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, request);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void insert_locationNull_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, ContainerNotFoundException {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of("value"))
                .build();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.insert(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, request);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void insert_locationAndDataNull_fails() {

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insert(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, null);
        });
    }

    @Test
    public void getAll_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, PaginationException, ContainerNotFoundException, QueryStoreException {

        /* test */
        dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, null, null, null);
    }

    @Test
    public void findAll_noPagination_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException,
            ContainerNotFoundException, QueryStoreException {
        final Long page = null;
        final Long size = null;

        /* test */
        dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
    }

    @Test
    public void findAll_pageNull_fails() {
        final Long page = null;
        final Long size = 1L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeNull_fails() {
        final Long page = 1L;
        final Long size = null;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_negativePage_fails() {
        final Long page = -1L;
        final Long size = 1L /* arbitrary */;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeZero_fails() {
        final Long page = 1L /* arbitrary */;
        final Long size = 0L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeNegative_fails() {
        final Long page = 1L /* arbitrary */;
        final Long size = -1L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void getAll_parameter2_fails() {
        final Long page = 1L;
        final Long size = 0L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void getAll_parameter_fails() {
        final Long page = -1L;
        final Long size = 10L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void getAllTotal_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException,
            PaginationException, ContainerNotFoundException, QueryStoreException {
        final Instant timestamp = Instant.now();

        /* test */
        final ResponseEntity<QueryResultDto> response = dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID,
                TABLE_1_ID, timestamp, null, null);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getAllCount_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException,
            PaginationException, ContainerNotFoundException, QueryStoreException {
        final Instant timestamp = Instant.now();

        /* test */
        final ResponseEntity<QueryResultDto> response = dataEndpoint.getAll(CONTAINER_1_ID, DATABASE_1_ID,
                TABLE_1_ID, timestamp, null, null);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("FDA-COUNT"));
    }

}
