package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositResponseDto;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.config.ReadyConfig;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FileServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoFileService fileService;

    @MockBean
    private RestTemplate apiTemplate;

    @MockBean
    private TableRepository tableRepository;

    @Test
    public void createResource_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException, MetadataDatabaseNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* mock */
        when(apiTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(FILE_1));
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();

        /* test */
        final FileResponseDto response = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        assertEquals(FILE_1_NAME, response.getFilename());
        assertEquals(FILE_1_CHECKSUM, response.getChecksum());
        assertEquals(FILE_1_SIZE, response.getFilesize());
    }

    @Test
    public void createResource_notExists_fails() throws IOException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* mock */
        when(apiTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        });
    }

    @Test
    public void createResource_bodyEmpty_fails() throws IOException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* mock */
        when(apiTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileResponseDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.accepted().body(null));
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        });
    }

    @Test
    public void listAll_succeeds() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoAuthenticationException {

        /* mock */
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileResponseDto[].class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.ok().body(new FileResponseDto[]{FILE_1}));
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final List<FileResponseDto> response = fileService.listAll(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(1, response.size());
    }

    @Test
    public void listAll_noContent_fails() {

        /* mock */
        when(apiTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileResponseDto[].class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.ok().body(null));
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            fileService.listAll(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void listAll_notFound_fails() {

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            fileService.listAll(DATABASE_1_ID, 9999L);
        });
    }

}