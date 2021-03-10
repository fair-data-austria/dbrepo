package at.tuwien.base;

import at.tuwien.entities.DatabaseContainer;
import at.tuwien.repositories.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

@SpringBootTest()
@Log4j2
public abstract class BaseIntegrationTest {

    @Value("${server.context-path}")
    private String contextPath;

    @LocalServerPort
    private int port;

    @MockBean
    MetadataRepository metadataRepository;

    @Before
    public void beforeBase() {
        BDDMockito.when(metadataRepository.findAll())
                .thenReturn(List.of(
                        DatabaseContainer.builder()
                                .id("deadbeef")
                                .build()
                ));
    }
}