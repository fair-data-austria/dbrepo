package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import at.tuwien.service.TableService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
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
    private TableService tableService;

    @MockBean
    private TableRepository tableRepository;

    @MockBean
    private DatabaseRepository databaseRepository;

    @Autowired
    private TableEndpoint tableEndpoint;

    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException, TableNotFoundException {
        when(tableService.findAll(DATABASE_1_ID))
                .thenReturn(List.of(TABLE_1));

        /* test */
        final ResponseEntity<List<TableBriefDto>> response = tableEndpoint.findAll(DATABASE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, TableNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, ParserConfigurationException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS5)
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(tableService.findById(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(TABLE_1);

        /* test */
        final ResponseEntity<TableBriefDto> response = tableEndpoint.create(DATABASE_1_ID, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void create_databaseNotFound_fails() throws DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException, ArbitraryPrimaryKeysException, ParserConfigurationException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS5)
                .build();
        when(tableService.create(DATABASE_1_ID, request))
                .thenAnswer(invocation -> {
                    throw new DatabaseNotFoundException("no db");
                });

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableEndpoint.create(DATABASE_1_ID, request);
        });
    }

    @Test
    public void create_tableNotFound_fails() throws DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException, ArbitraryPrimaryKeysException, ParserConfigurationException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS5)
                .build();
        when(tableService.create(DATABASE_1_ID, request))
                .thenAnswer(invocation -> {
                    throw new TableNotFoundException("no table");
                });

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableEndpoint.create(DATABASE_1_ID, request);
        });
    }

    @Disabled("not throwing")
    @Test
    public void create_notPostgres_fails() {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS5)
                .build();
        final Database DATABASE_2 = DATABASE_1;
        DATABASE_2.getContainer().getImage().setRepository("mariadb");
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(databaseRepository.findById(DATABASE_2.getId()))
                .thenReturn(Optional.of(DATABASE_2));

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            tableEndpoint.create(DATABASE_1_ID, request);
        });
    }

    @Disabled("not throwing")
    @Test
    public void create_notSql_fails() throws TableNotFoundException, SQLException, DatabaseNotFoundException, ImageNotSupportedException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .description(TABLE_1_DESCRIPTION)
                .columns(COLUMNS5)
                .build();
        when(tableService.findById(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(TABLE_1);

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            tableEndpoint.create(DATABASE_1_ID, request);
        });
    }

    @Test
    public void findById_succeeds() throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException {
        when(tableService.findById(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(TABLE_1);

        /* test */
        final ResponseEntity<TableDto> response = tableEndpoint.findById(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TABLE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(TABLE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Disabled("not throwing")
    @Test
    public void findById_notFound_fails() throws TableNotFoundException {
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableEndpoint.findById(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Disabled("not throwing")
    @Test
    public void delete_notSql_fails() {

    }

    @Test
    public void delete_notFound_fails() throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DataProcessingException, DatabaseNotFoundException, ImageNotSupportedException {
        doThrow(TableNotFoundException.class)
                .when(tableService)
                .delete(DATABASE_1_ID, TABLE_1_ID);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableEndpoint.delete(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DataProcessingException, DatabaseNotFoundException, ImageNotSupportedException {
        /* test */
        tableEndpoint.delete(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void update_fails() {
        /* test */
        final ResponseEntity<TableBriefDto> response = tableEndpoint.update(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Disabled
    @Test
    public void insert() {

    }

    @Disabled
    @Test
    public void showData() {

    }
}
