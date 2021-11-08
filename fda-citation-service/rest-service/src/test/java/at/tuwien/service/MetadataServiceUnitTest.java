package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.DepositTzDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MetadataServiceUnitTest extends BaseUnitTest {

    @Autowired
    private ZenodoMetadataService zenodoService;

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    @Qualifier("zenodoTemplate")
    private RestTemplate zenodoTemplate;

    @MockBean
    private QueryRepository queryRepository;

    @MockBean
    private DatabaseRepository databaseRepository;

    @MockBean
    private TableRepository tableRepository;

    @Test
    public void listCitations_succeeds() throws MetadataDatabaseNotFoundException {

        /* mocks */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(queryRepository.findByDatabase(DATABASE_1))
                .thenReturn(List.of(QUERY_1));

        /* test */
        final List<Query> response = zenodoService.listCitations(DATABASE_1_ID);
        assertEquals(1, response.size());
        assertEquals(QUERY_1, response.get(0));
    }

    @Test
    public void storeCitation_succeed() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoUnavailableException, MetadataDatabaseNotFoundException {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(queryRepository.save(Mockito.any()))
                .thenReturn(QUERY_1);
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.POST), Mockito.any(),
                eq(DepositTzDto.class), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .body(DEPOSIT_1));

        /* test */
        final Query response = zenodoService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(QUERY_1_ID, response.getId());
        assertEquals(DEPOSIT_1_ID, response.getDepositId());
    }

    @Test
    public void storeCitation_unavailable_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.POST), Mockito.any(), eq(DepositTzDto.class),
                        anyString());

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void storeCitation_notFound_fails() {

        /* mocks */
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            zenodoService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoUnavailableException, QueryNotFoundException,
            MetadataDatabaseNotFoundException {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(),
                eq(String.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .build());

        /* test */
        zenodoService.deleteCitation(DATABASE_1_ID, QUERY_1_ID);
    }

    @Test
    public void deleteCitation_unavailable_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(),
                        eq(String.class), eq(DEPOSIT_1_ID), anyString());

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            zenodoService.deleteCitation(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteCitation_notFound_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.empty());
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(),
                        eq(String.class), eq(DEPOSIT_1_ID), anyString());

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            zenodoService.deleteCitation(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteCitation_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(),
                eq(String.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoService.deleteCitation(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void updateCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoUnavailableException, QueryNotFoundException, MetadataDatabaseNotFoundException {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.any(),
                eq(DepositTzDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.ok(DEPOSIT_1));

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        final Query response = zenodoService.updateCitation(DATABASE_1_ID, QUERY_1_ID, request);
        assertEquals(QUERY_1_ID, response.getId());
    }

    @Test
    public void updateCitation_unavailable_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.PUT), Mockito.any(),
                        eq(DepositTzDto.class), eq(DEPOSIT_1_ID), anyString());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, QUERY_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notExists_fails() {

        /* mocks */
        when(queryRepository.findByDatabaseAndId(DATABASE_1, QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.any(),
                eq(DepositTzDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, QUERY_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notFound_fails() {

        /* mocks */

        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.empty());
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.any(),
                eq(DepositTzDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, QUERY_1_ID, request);
        });
    }

}