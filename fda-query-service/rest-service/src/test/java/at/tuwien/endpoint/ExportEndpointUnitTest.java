package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
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
public class ExportEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ExportEndpoint exportEndpoint;

    @MockBean
    private QueryServiceImpl queryService;

    @MockBean
    private CommaValueService commaValueService;

    @Test
    public void export_timestampNull_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException,
            PaginationException, ContainerNotFoundException {

        /* test */
        final ResponseEntity<InputStreamResource> response = exportEndpoint.export(CONTAINER_1_ID, DATABASE_1_ID,
                TABLE_1_ID, null);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void export_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException, PaginationException,
            ContainerNotFoundException {
        final Instant request = Instant.now()
                .minusMillis(1000 * 1000);

        /* test */
        final ResponseEntity<InputStreamResource> response = exportEndpoint.export(CONTAINER_1_ID, DATABASE_1_ID,
                TABLE_1_ID, request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void export_inFuture_succeeds() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, FileStorageException,
            PaginationException, ContainerNotFoundException {
        final Instant request = Instant.now()
                .plusMillis(1000 * 1000);

        /* test */
        final ResponseEntity<InputStreamResource> response = exportEndpoint.export(CONTAINER_1_ID, DATABASE_1_ID,
                TABLE_1_ID, request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
