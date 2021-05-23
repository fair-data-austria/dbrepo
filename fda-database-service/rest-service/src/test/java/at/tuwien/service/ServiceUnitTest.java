package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.Database;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ServiceUnitTest extends BaseIntegrationTest {

    @Autowired
    private DatabaseService databaseService;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private ContainerRepository containerRepository;

    @MockBean
    private PostgresService postgresService;

    @Test
    public void findAll_succeeds() {
        when(databaseRepository.findAll())
                .thenReturn(List.of(DATABASE_1));

        final List<Database> response = databaseService.findAll();

        /* test */
        assertEquals(1, response.size());
        assertEquals(DATABASE_1, response.get(0));
    }

    @Test
    public void findById_succeeds() throws DatabaseNotFoundException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));

        final Database response = databaseService.findById(DATABASE_1_ID);

        /* test */
        assertEquals(DATABASE_1, response);
    }

    @Test
    public void findById_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.findById(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_succeeds() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));

        /* test */
        databaseService.delete(DATABASE_1_ID);
    }

    @Test
    public void delete_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notPostgres_fails() {
        final Database notPostgresDatabase = DATABASE_1;
        notPostgresDatabase.getContainer().getImage().setRepository("mariadb");
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(notPostgresDatabase));

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseService.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void create_succeeds() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .containerId(CONTAINER_1_ID)
                .build();
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(CONTAINER_1));
        when(databaseRepository.save(any()))
                .thenReturn(DATABASE_1);

        final Database response = databaseService.create(request);

        /* test */
        assertEquals(DATABASE_1, response);
    }

    @Test
    public void create_notFound_fails() {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .containerId(CONTAINER_1_ID)
                .build();
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseService.create(request);
        });
    }

    @Test
    public void create_notSupported_fails() {
        final Container notPostgresContainer = CONTAINER_1;
        notPostgresContainer.getImage().setRepository("mariadb");
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .name(DATABASE_1_NAME)
                .containerId(CONTAINER_1_ID)
                .build();
        when(containerRepository.findById(CONTAINER_1_ID))
                .thenReturn(Optional.of(notPostgresContainer));

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseService.create(request);
        });
    }
}