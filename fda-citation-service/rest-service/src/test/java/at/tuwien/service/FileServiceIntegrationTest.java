package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FileServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private TableRepository tableRepository;

    @Autowired
    private ZenodoFileService fileService;

    @Autowired
    private ZenodoMetadataService metadataService;

    @Test
    public void createResource_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException, MetadataDatabaseNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();

        /* mock */
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final FileResponseDto response = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        assertEquals(FILE_1_NAME, response.getFilename());
        assertEquals(FILE_1_CHECKSUM, response.getChecksum());
        assertEquals(FILE_1_SIZE, response.getFilesize());
    }

    @Test
    public void createResource_largeFile_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException, MetadataDatabaseNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/weatherAUS.csv");

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_2_NAME)
                .build();

        /* mock */
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final FileResponseDto response = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        assertEquals(FILE_2_NAME, response.getFilename());
        assertEquals(FILE_2_CHECKSUM, response.getChecksum());
        assertEquals(FILE_2_SIZE, response.getFilesize());
    }

    @Test
    public void listAll_notFound_fails() {

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            fileService.listResources(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void listAll_succeeds() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoAuthenticationException, FileNotFoundException, ZenodoFileTooLargeException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        final FileResponseDto fileResponse = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, upload, file);

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final List<FileResponseDto> listResponse = fileService.listResources(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(1, listResponse.size());
        assertEquals(FILE_1_CHECKSUM, listResponse.get(0).getChecksum());
        assertEquals(fileResponse.getId(), listResponse.get(0).getId());
    }

    @Test
    public void findResource_noContent_fails() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoFileTooLargeException, ZenodoNotFoundException, ZenodoAuthenticationException, FileNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        final FileResponseDto fileResponse = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, upload, file);

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final FileResponseDto findResponse = fileService.findResource(DATABASE_1_ID, TABLE_1_ID, fileResponse.getId());
        assertEquals(FILE_1_CHECKSUM, findResponse.getChecksum());
        assertEquals(fileResponse.getId(), findResponse.getId());
    }

    @Test
    public void deleteRessource_succeeds() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoFileTooLargeException, ZenodoNotFoundException, ZenodoAuthenticationException, FileNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        final FileResponseDto fileResponse = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, upload, file);

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        fileService.deleteResource(DATABASE_1_ID, TABLE_1_ID, fileResponse.getId());
    }

}