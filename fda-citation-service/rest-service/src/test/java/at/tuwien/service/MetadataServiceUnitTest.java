package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.DepositTzDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private RestTemplate apiTemplate;

    @MockBean
    private TableRepository tableRepository;

    final Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .build();

    @Test
    public void listCitations_succeeds() {

        /* mocks */
        when(zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID))
                .thenReturn(List.of(QUERY_1));

        /* test */
        final List<Query> response = zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(1, response.size());
        assertEquals(QUERY_1, response.get(0));
    }

    @Test
    public void storeCitation_succeed() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.POST), Mockito.<HttpEntity<String>>any(), eq(DepositTzDto.class),
                anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .body(DEPOSIT_1));

        /* test */
        final Query response = zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(QUERY_1_ID, response.getExecutionTimestamp());
    }

    @Test
    public void storeCitation_unavailable_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.POST), Mockito.<HttpEntity<String>>any(), eq(DepositTzDto.class),
                        anyString());

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void deleteCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoUnavailableException, QueryNotFoundException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class), anyLong(),
                anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .build());

        /* test */
        zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID);
    }

    @Test
    public void deleteCitation_unavailable_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class), anyLong(),
                        anyString());

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteCitation_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class), anyLong(),
                anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void updateCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, ZenodoUnavailableException, QueryNotFoundException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositTzDto>>any(), eq(DepositTzDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(DEPOSIT_1));

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        final Query response = zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, request);
        assertEquals(QUERY_1_ID, response.getId());
    }

    @Test
    public void updateCitation_unavailable_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositTzDto>>any(), eq(DepositTzDto.class),
                        eq(DEPOSIT_1_ID), anyString());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notExists_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositTzDto>>any(), eq(DepositTzDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notFound_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.empty());
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositTzDto>>any(),
                eq(DepositTzDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, request);
        });
    }

}