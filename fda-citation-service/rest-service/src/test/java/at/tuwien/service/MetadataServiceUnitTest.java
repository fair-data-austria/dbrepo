package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.deposit.DepositResponseDto;
import at.tuwien.api.zenodo.deposit.DepositChangeRequestDto;
import at.tuwien.api.zenodo.deposit.MetadataDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
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
    public void listCitations_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoUnavailableException {

        /* mocks */
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(DepositResponseDto[].class),
                anyString()))
                .thenReturn(ResponseEntity.ok(new DepositResponseDto[]{DEPOSIT_2}));

        /* test */
        final List<DepositResponseDto> response = zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(1, response.size());
    }

    @Test
    public void listCitations_unavailable_fails() {

        /* mocks */
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(DepositResponseDto[].class),
                        anyString());

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void listCitations_empty_fails() {

        /* mocks */
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(DepositResponseDto[].class),
                anyString()))
                .thenReturn(ResponseEntity.ok().build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void storeCitation_succeed() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.POST), Mockito.<HttpEntity<String>>any(), eq(DepositChangeResponseDto.class),
                anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .body(DEPOSIT_1));

        /* test */
        final DepositChangeResponseDto response = zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(DEPOSIT_1_CREATED, response.getCreated());
        assertEquals(DEPOSIT_1_MODIFIED, response.getModified());
    }

    @Test
    public void storeCitation_unavailable_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.POST), Mockito.<HttpEntity<String>>any(), eq(DepositChangeResponseDto.class),
                        anyString());

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void deleteCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoNotFoundException, ZenodoUnavailableException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class), anyLong(),
                anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .build());

        /* test */
        zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID);
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
            zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID);
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
            zenodoService.deleteCitation(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void updateCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(DEPOSIT_1));

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
    }

    @Test
    public void updateCitation_unavailable_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        doThrow(ResourceAccessException.class)
                .when(apiTemplate)
                .exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class),
                        eq(DEPOSIT_1_ID), anyString());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void updateCitation_only1orcid_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final MetadataDto m = METADATA_1;
        m.getCreators()[1].setOrcid(null);
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(m)
                .build();

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notExists_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notFound_fails() {

        /* mocks */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.empty());
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(),
                eq(DepositChangeResponseDto.class),
                eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

}