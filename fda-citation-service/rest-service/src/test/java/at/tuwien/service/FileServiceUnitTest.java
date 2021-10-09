package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.File;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.FileRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FileServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoFileService fileService;

    @MockBean
    @Qualifier("zenodoTemplate")
    private RestTemplate zenodoTemplate;

    @MockBean
    @Qualifier("queryTemplate")
    private RestTemplate queryTemplate;

    @MockBean
    private TableRepository tableRepository;

    @MockBean
    private QueryRepository queryRepository;

    @MockBean
    private FileRepository fileRepository;

    final Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .build();

    @Test
    public void createResource_succeeds() throws ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException,
            RemoteDatabaseException, TableServiceException, ZenodoFileException, MetadataDatabaseNotFoundException {

        /* mock */
        when(zenodoTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK)
                        .body(FILE_1_DTO));
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(queryTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(QueryResultDto.class),
                eq(DATABASE_1_ID), eq(QUERY_1_ID)))
                .thenReturn(ResponseEntity.ok(QUERY_1_RESULT));

        /* test */
        final File response = fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(FILE_1_ID, response.getId());
    }

    @Test
    public void createResource_unavailable_fails() {

        /* mock */
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                        eq(FileDto.class), eq(DEPOSIT_1_ID), anyString());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));
        when(queryTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(QueryResultDto.class),
                eq(DATABASE_1_ID), eq(QUERY_1_ID)))
                .thenReturn(ResponseEntity.ok(QUERY_1_RESULT));

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void createResource_notExists_fails() {

        /* mock */
        when(zenodoTemplate.postForEntity(anyString(), Mockito.any(), eq(FileDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .build());
        when(queryTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(QueryResultDto.class),
                eq(DATABASE_1_ID), eq(QUERY_1_ID)))
                .thenReturn(ResponseEntity.ok(QUERY_1_RESULT));
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoNotFoundException.class, () -> {
            fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void createResource_bodyEmpty_fails() {

        /* mock */
        when(zenodoTemplate.postForEntity(anyString(), Mockito.<MultiValueMap<String, HttpEntity<?>>>any(),
                eq(FileDto.class), eq(DEPOSIT_1_ID), anyString()))
                .thenReturn(ResponseEntity.accepted().body(null));
        when(queryTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(QueryResultDto.class),
                eq(DATABASE_1_ID), eq(QUERY_1_ID)))
                .thenReturn(ResponseEntity.ok(QUERY_1_RESULT));
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void listAll_succeeds() {

        /* mock */
        doReturn(List.of(FILE_1))
                .when(fileRepository)
                .findAll();
        when(queryRepository.findAll())
                .thenReturn(List.of(QUERY_1));

        /* test */
        final List<File> response = fileService.listResources();
        assertEquals(1, response.size());
    }

    @Test
    public void findResource_succeeds() throws ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException,
            MetadataDatabaseNotFoundException {

        /* mock */
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileDto.class),
                eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString()))
                .thenReturn(ResponseEntity.ok().body(FILE_1_DTO));
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        final File file = fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(FILE_1_ID, file.getId());
    }

    @Test
    public void findResource_unavailable_fails() {

        /* mock */
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileDto.class),
                        eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void findResource_noContent_fails() {

        /* mock */
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileDto.class),
                eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString()))
                .thenReturn(ResponseEntity.ok().body(null));
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void findResource_notFound_remote_fails() {

        /* mock */
        doThrow(HttpClientErrorException.NotFound.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.GET), Mockito.any(), eq(FileDto.class),
                        eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(MetadataDatabaseNotFoundException.class, () -> {
            fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void findResource_notFound_local_fails() {

        /* mock */
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.empty());

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteResource_succeeds() throws ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException,
            MetadataDatabaseNotFoundException {

        /* mock */
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class),
                eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        fileService.deleteResource(DATABASE_1_ID, QUERY_1_ID);
    }

    @Test
    public void deleteResource_unavailable_fails() {

        /* mock */
        doThrow(ResourceAccessException.class)
                .when(zenodoTemplate)
                .exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class),
                        eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoUnavailableException.class, () -> {
            fileService.deleteResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void deleteResource_wrongStatus_fails() {

        /* mock */
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.DELETE), Mockito.any(), eq(String.class),
                eq(DEPOSIT_1_ID), eq(FILE_1_ID), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
        when(queryRepository.findById(QUERY_1_ID))
                .thenReturn(Optional.of(QUERY_1));

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            fileService.deleteResource(DATABASE_1_ID, QUERY_1_ID);
        });
    }

}