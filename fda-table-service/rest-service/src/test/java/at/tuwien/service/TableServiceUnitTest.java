package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.TableServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private TableServiceImpl tableService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private TableRepository tableRepository;

    @BeforeAll
    public static void beforeAll() {
        TABLE_1.setDatabase(DATABASE_1);
    }

    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabase(DATABASE_1))
                .thenReturn(List.of(TABLE_1));

        /* test */
        final List<Table> response = tableService.findAll(CONTAINER_1_ID, DATABASE_1_ID);
        assertEquals(1, response.size());
        assertEquals(TABLE_1_ID, response.get(0).getId());
    }

    @Test
    public void findAll_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableService.findAll(CONTAINER_1_ID, DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            tableService.deleteTable(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void delete_noSql_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            tableService.deleteTable(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void findById_succeeds() throws TableNotFoundException, DatabaseNotFoundException,
            ContainerNotFoundException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final Table response = tableService.findById(CONTAINER_1_ID, DATABASE_1_ID, TABLE_1_ID);
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
            tableService.findById(CONTAINER_1_ID, DATABASE_1_ID, 9999L);
        });
    }

}
