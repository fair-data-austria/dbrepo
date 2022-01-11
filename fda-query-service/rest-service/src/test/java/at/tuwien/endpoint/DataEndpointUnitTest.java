package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DataEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private DataEndpoint dataEndpoint;

    @MockBean
    private QueryServiceImpl queryService;

    @MockBean
    private CommaValueService commaValueService;

    @Test
    public void insert_succeeds() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException {
        final String request = "test:csv/csv_01.csv";

        /* test */
        final ResponseEntity<?> response = dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, request, null);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void insert_locationNull_succeeds() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of("key", "value")))
                .build();

        /* test */
        final ResponseEntity<?> response = dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, null, request);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void insert_locationAndDataNull_fails() {

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, null, null);
        });
    }

    @Test
    public void insert_locationAndDataNotNull_fails() {
        final String location = "";
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of("key", "value")))
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insert(DATABASE_1_ID, TABLE_1_ID, location, request);
        });
    }

    @Test
    public void getAll_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, PaginationException {

        /* test */
        dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, null, null, null);
    }

    @Test
    public void findAll_noPagination_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException {
        final Long page = null;
        final Long size = null;

        /* test */
        dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
    }

    @Test
    public void findAll_pageNull_fails() {
        final Long page = null;
        final Long size = 1L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeNull_fails() {
        final Long page = 1L;
        final Long size = null;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_negativePage_fails() {
        final Long page = -1L;
        final Long size = 1L /* arbitrary */;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeZero_fails() {
        final Long page = 1L /* arbitrary */;
        final Long size = 0L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void findAll_sizeNegative_fails() {
        final Long page = 1L /* arbitrary */;
        final Long size = -1L;

        /* test */
        assertThrows(PaginationException.class, () -> {
            dataEndpoint.getAll(DATABASE_1_ID, TABLE_1_ID, DATABASE_1_CREATED, page, size);
        });
    }

    @Test
    public void export_timestampNull_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, PaginationException {

        /* test */
        final ResponseEntity<InputStreamResource> respone = dataEndpoint.export(DATABASE_1_ID, TABLE_1_ID, null);
        assertNotNull(respone);
        assertEquals(HttpStatus.OK, respone.getStatusCode());
    }

    @Test
    public void export_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, PaginationException {
        final Instant request = Instant.now()
                .minusMillis(1000 * 1000);

        /* test */
        final ResponseEntity<InputStreamResource> respone = dataEndpoint.export(DATABASE_1_ID, TABLE_1_ID, request);
        assertNotNull(respone);
        assertEquals(HttpStatus.OK, respone.getStatusCode());

    }

    @Test
    public void export_inFuture_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, PaginationException {
        final Instant request = Instant.now()
                .plusMillis(1000 * 1000);

        /* test */
        final ResponseEntity<InputStreamResource> respone = dataEndpoint.export(DATABASE_1_ID, TABLE_1_ID, request);
        assertNotNull(respone);
        assertEquals(HttpStatus.OK, respone.getStatusCode());
    }

}
