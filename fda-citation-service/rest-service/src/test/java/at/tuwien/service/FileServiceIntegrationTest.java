package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoFileTooLargeException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FileServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoFileService fileService;

    @Autowired
    private ZenodoMetadataService metadataService;

    @Test
    public void createResource_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException {
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* test */
        final FileResponseDto response = fileService.createResource(deposit.getId(), FILE_1_NAME, file);
        assertEquals(FILE_1_NAME, response.getFilename());
        assertEquals(FILE_1_CHECKSUM, response.getChecksum());
        assertEquals(FILE_1_SIZE, response.getFilesize());
    }

    @Test
    public void createResource_largeFile_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException {
        final DepositChangeResponseDto deposit = metadataService.storeCitation();
        final File file = ResourceUtils.getFile("classpath:csv/weatherAUS.csv");

        /* test */
        final FileResponseDto response = fileService.createResource(deposit.getId(), FILE_2_NAME, file);
        assertEquals(FILE_2_NAME, response.getFilename());
        assertEquals(FILE_2_CHECKSUM, response.getChecksum());
        assertEquals(FILE_2_SIZE, response.getFilesize());
    }

}