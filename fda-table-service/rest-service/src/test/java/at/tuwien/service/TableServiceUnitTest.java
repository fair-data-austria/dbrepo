package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableDto;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import org.junit.jupiter.api.BeforeAll;
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
public class TableServiceUnitTest extends BaseUnitTest {

    @Autowired
    private TableService tableService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private TableRepository tableRepository;

    @MockBean
    private PostgresService postgresService;

    @BeforeAll
    public static void beforeAll() {
        TABLE_1.setDatabase(DATABASE_1);
    }

    @Test
    public void findAll_succeeds() throws TableNotFoundException, DatabaseNotFoundException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabase(DATABASE_1))
                .thenReturn(List.of(TABLE_1));

        /* test */
        final List<Table> response = tableService.findAll(DATABASE_1_ID);
        assertEquals(1, response.size());
        assertEquals(TABLE_1_ID, response.get(0).getId());
    }

    @Test
    public void findAll_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableService.findAll(DATABASE_1_ID);
        });
    }

    @Disabled("invalid mock")
    @Test
    public void delete_succeeds() throws TableNotFoundException, DatabaseConnectionException, TableMalformedException,
            DataProcessingException, DatabaseNotFoundException, ImageNotSupportedException {
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doNothing()
                .when(postgresService)
                .deleteTable(TABLE_1);
        doNothing()
                .when(tableRepository)
                .deleteById(TABLE_1_ID);

        /* test */
        tableService.delete(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void delete_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableService.delete(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_noConnection_fails() throws DatabaseConnectionException, TableMalformedException,
            DataProcessingException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doAnswer(invocation -> new TableMalformedException("no connection"))
                .when(postgresService)
                .deleteTable(TABLE_1);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableService.delete(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_noSql_fails() throws DataProcessingException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableService.delete(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void findById_succeeds() throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final Table response = tableService.findById(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(TABLE_1_ID, response.getId());
        assertEquals(TABLE_1_NAME, response.getName());
    }

    @Test
    public void findById_noTable_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, 9999L))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableService.findById(DATABASE_1_ID, 9999L);
        });
    }

    @Test
    public void create_succeeds() throws DatabaseConnectionException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException, ArbitraryPrimaryKeysException, ParserConfigurationException, EntityNotSupportedException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .columns(COLUMNS5)
                .description(TABLE_1_DESCRIPTION)
                .build();
        when(tableRepository.save(any()))
                .thenReturn(TABLE_1);
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doNothing()
                .when(postgresService)
                .createTable(DATABASE_1, request);

        /* test */
        final Table response = tableService.create(DATABASE_1_ID, request);
        assertEquals(TABLE_1_ID, response.getId());
        assertEquals(TABLE_1_NAME, response.getName());
    }

    @Disabled("cannot yet test private method")
    @Test
    public void create_noPostgres_fails() {

    }

    @Disabled("cannot yet test private method")
    @Test
    public void create_noConnection_fails() {

    }

    @Disabled("invalid mock")
    @Test
    public void create_noSql_fails() throws DataProcessingException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_1_NAME)
                .columns(COLUMNS5)
                .description(TABLE_1_DESCRIPTION)
                .build();
        when(tableRepository.save(any()))
                .thenReturn(TABLE_1);
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doAnswer(invocation -> new TableMalformedException("no sql"))
                .when(postgresService.getCreateTableStatement(any(), request));

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            tableService.create(DATABASE_1_ID, request);
        });
    }

}
