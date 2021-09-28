package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.deposit.DepositResponseDto;
import at.tuwien.api.zenodo.deposit.DepositChangeRequestDto;
import at.tuwien.api.zenodo.deposit.MetadataDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MetadataServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoMetadataService zenodoService;

    @MockBean
    private RestTemplate zenodoTemplate;

    @Test
    public void listCitations_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(DepositResponseDto[].class), anyString()))
                .thenReturn(ResponseEntity.ok(new DepositResponseDto[]{DEPOSIT_2}));

        /* test */
        final List<DepositResponseDto> response = zenodoService.listCitations();
        assertEquals(1, response.size());
    }

    @Test
    public void listCitations_empty_fails() {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(DepositResponseDto[].class), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoService.listCitations();
        });
    }

    @Test
    public void storeCitation_succeed() throws ZenodoApiException, ZenodoAuthenticationException {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.POST), Mockito.<HttpEntity<String>>any(), eq(DepositChangeResponseDto.class), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .body(DEPOSIT_1));

        /* test */
        final DepositChangeResponseDto response = zenodoService.storeCitation();
        assertEquals(DEPOSIT_1_CREATED, response.getCreated());
        assertEquals(DEPOSIT_1_MODIFIED, response.getModified());
    }

    @Test
    public void deleteCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(String.class), anyLong(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .build());

        /* test */
        zenodoService.deleteCitation(DEPOSIT_1_ID);
    }

    @Test
    public void deleteCitation_fails() {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), eq(null), eq(String.class), anyLong(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoService.deleteCitation(DEPOSIT_1_ID);
        });
    }

    @Test
    public void updateCitation_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException {
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(DEPOSIT_1));

        /* test */
        zenodoService.updateCitation(DEPOSIT_1_ID, request);
    }

    @Test
    public void updateCitation_only1orcid_fails() {
        final MetadataDto m = METADATA_1;
        m.getCreators()[1].setOrcid(null);
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(m)
                .build();
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            zenodoService.updateCitation(DEPOSIT_1_ID, request);
        });
    }

    @Test
    public void updateCitation_notExists_fails() {
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.PUT), Mockito.<HttpEntity<DepositChangeRequestDto>>any(), eq(DepositChangeResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            zenodoService.updateCitation(DEPOSIT_1_ID, request);
        });
    }

}