package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    final Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .build();

    @Test
    public void createResource_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException, MetadataDatabaseNotFoundException,
            ZenodoUnavailableException {
        final MockMultipartFile file = new MockMultipartFile("testdata.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/testdata.csv")));

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();

        /* mock */
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();

        /* test */
        final FileResponseDto response = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        assertEquals(FILE_1_NAME, response.getFilename());
        assertEquals(FILE_1_CHECKSUM, response.getChecksum());
        assertEquals(FILE_1_SIZE, response.getFilesize());
    }

    @Test
    @Disabled("slow internet")
    public void createResource_largeFile_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException, MetadataDatabaseNotFoundException,
            ZenodoUnavailableException {
        final MockMultipartFile file = new MockMultipartFile("weatherAUS.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/weatherAUS.csv")));

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_2_NAME)
                .build();

        /* mock */
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();

        /* test */
        final FileResponseDto response = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);
        assertEquals(FILE_2_NAME, response.getFilename());
        assertEquals(FILE_2_CHECKSUM, response.getChecksum());
        assertEquals(FILE_2_SIZE, response.getFilesize());
    }

    @Test
    public void listAll_notFound_fails() {
        final Table TABLE_1 = Table.builder()
                .id(-1L)
                .build();

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            fileService.listResources(DATABASE_1_ID, TABLE_1_ID);
        });
    }

    @Test
    public void listAll_succeeds() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoAuthenticationException, IOException, ZenodoFileTooLargeException,
            ZenodoUnavailableException {
        final MockMultipartFile file = new MockMultipartFile("testdata.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/testdata.csv")));

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
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
            ZenodoFileTooLargeException, ZenodoNotFoundException, ZenodoAuthenticationException,
            ZenodoUnavailableException, IOException {
        final MockMultipartFile file = new MockMultipartFile("testdata.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/testdata.csv")));

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
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
            ZenodoFileTooLargeException, ZenodoNotFoundException, ZenodoAuthenticationException, IOException,
            ZenodoUnavailableException {
        final MockMultipartFile file = new MockMultipartFile("testdata.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/testdata.csv")));

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* request */
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto upload = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();
        final FileResponseDto fileResponse = fileService.createResource(DATABASE_1_ID, TABLE_1_ID, upload, file);

        /* mock */
        when(tableRepository.findById(TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        fileService.deleteResource(DATABASE_1_ID, TABLE_1_ID, fileResponse.getId());
    }

}