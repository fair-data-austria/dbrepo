package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoFileTooLargeException;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
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

    @Test
    public void createResource_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoFileTooLargeException {

        /* mock */
        when(apiTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileResponseDto.class), anyLong(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(FILE_1));

        /* request */
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* test */
        final FileResponseDto response = fileService.createResource(DEPOSIT_1_ID, FILE_1_NAME, file);
        assertEquals(FILE_1_NAME, response.getFilename());
        assertEquals(FILE_1_CHECKSUM, response.getChecksum());
        assertEquals(FILE_1_SIZE, response.getFilesize());
    }

    @Test
    public void createResource_notExists_fails() throws IOException {

        /* mock */
        when(apiTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileResponseDto.class), anyLong(), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());

        /* request */
        final File file = ResourceUtils.getFile("classpath:csv/testdata.csv");

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            fileService.createResource(DEPOSIT_1_ID, FILE_1_NAME, file);
        });
    }

}