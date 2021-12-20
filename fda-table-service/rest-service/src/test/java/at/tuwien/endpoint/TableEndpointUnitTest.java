package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.MessageQueueService;
import at.tuwien.service.impl.TableServiceImpl;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private TableServiceImpl tableService;

    @MockBean
    private TableRepository tableRepository;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private MessageQueueService messageQueueService;

    @Autowired
    private TableEndpoint tableEndpoint;

    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException {
        when(tableService.findAllForDatabaseId(DATABASE_1_ID))
                .thenReturn(List.of(TABLE_1));

        /* test */
        final ResponseEntity<List<TableBriefDto>> response = tableEndpoint.findAll(DATABASE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            TableNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException, AmqpException, IOException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build();

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(tableService.findById(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(TABLE_1);
        doNothing()
                .when(messageQueueService)
                .create(TABLE_1);

        /* test */
        final ResponseEntity<TableBriefDto> response = tableEndpoint.create(DATABASE_1_ID, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void create_databaseNotFound_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build();
        when(tableService.createTable(DATABASE_1_ID, request))
                .thenAnswer(invocation -> {
                    throw new DatabaseNotFoundException("no db");
                });

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableEndpoint.create(DATABASE_1_ID, request);
        });
    }

    @Test
    public void findById_succeeds() throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {
        when(tableService.findById(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(TABLE_1);

        /* test */
        final ResponseEntity<TableDto> response = tableEndpoint.findById(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TABLE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(TABLE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void findById_notFound_fails() throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.empty());
        doThrow(TableNotFoundException.class)
                .when(tableService)
                .findById(DATABASE_1_ID, TABLE_1_ID);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableEndpoint.findById(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() throws TableNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException {
        doThrow(TableNotFoundException.class)
                .when(tableService)
                .deleteTable(DATABASE_1_ID, TABLE_1_ID);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableEndpoint.delete(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_succeeds() throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException,
            DataProcessingException {
        /* test */
        tableEndpoint.delete(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void update_fails() {
        /* test */
        final ResponseEntity<TableBriefDto> response = tableEndpoint.update(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

}
