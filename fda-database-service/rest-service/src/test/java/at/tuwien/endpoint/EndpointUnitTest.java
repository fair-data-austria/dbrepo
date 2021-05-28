package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.DatabaseBriefDto;
import at.tuwien.api.database.DatabaseCreateDto;
import at.tuwien.endpoints.DatabaseEndpoint;
import at.tuwien.exception.*;
import at.tuwien.service.DatabaseService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class EndpointUnitTest extends BaseUnitTest {

    @MockBean
    private DatabaseService databaseService;

    @Autowired
    private DatabaseEndpoint databaseEndpoint;

    @Test
    public void findAll_succeeds() {
        when(databaseService.findAll())
                .thenReturn(List.of(DATABASE_1));

        final ResponseEntity<List<DatabaseBriefDto>> response = databaseEndpoint.findAll();

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void create_succeeds() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();
        when(databaseService.create(request))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseBriefDto> response = databaseEndpoint.create(request);

        /* test */
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void create_containerNotFound_fails() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(ContainerNotFoundException.class);

        /* test */
        assertThrows(ContainerNotFoundException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void create_imageNotSupported_fails() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(ImageNotSupportedException.class);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void create_noConnection_fails() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(DatabaseConnectionException.class);

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void create_syntaxInvalid_fails() throws DatabaseConnectionException, ImageNotSupportedException, ContainerNotFoundException, DatabaseMalformedException {
        final DatabaseCreateDto request = DatabaseCreateDto.builder()
                .containerId(CONTAINER_1_ID)
                .name(CONTAINER_1_NAME)
                .build();

        when(databaseService.create(request))
                .thenThrow(DatabaseMalformedException.class);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseEndpoint.create(request);
        });
    }

    @Test
    public void findById_succeeds() throws DatabaseNotFoundException {
        when(databaseService.findById(DATABASE_1_ID))
                .thenReturn(DATABASE_1);

        final ResponseEntity<DatabaseBriefDto> response = databaseEndpoint.findById(DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DATABASE_1_ID, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(DATABASE_1_NAME, Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void findById_notFound_fails() throws DatabaseNotFoundException {
        when(databaseService.findById(DATABASE_1_ID))
                .thenThrow(DatabaseNotFoundException.class);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.findById(DATABASE_1_ID);
        });
    }

    @Disabled(value = "not implemented yet")
    @Test
    public void modify_succeeds() {
        //
    }

    @Disabled(value = "not implemented yet")
    @Test
    public void modify_malformedPayload_fails() {
        //
    }

    @Disabled(value = "not implemented yet")
    @Test
    public void modify_notFound_fails() {
        //
    }

    @Test
    public void delete_succeeds() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        final ResponseEntity<?> response = databaseEndpoint.delete(DATABASE_1_ID);

        /* test */
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void delete_invalidSyntax_fails() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        willThrow(DatabaseMalformedException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(DatabaseMalformedException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_invalidImage_fails() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        willThrow(ImageNotSupportedException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(ImageNotSupportedException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_notFound_fails() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        willThrow(DatabaseNotFoundException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }

    @Test
    public void delete_noConnection_fails() throws DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseMalformedException {
        willThrow(DatabaseConnectionException.class)
                .given(databaseService)
                .delete(DATABASE_1_ID);

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            databaseEndpoint.delete(DATABASE_1_ID);
        });
    }
}
