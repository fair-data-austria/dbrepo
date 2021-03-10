package base;

import at.tuwien.FdaContainerManagingApplication;
import at.tuwien.entities.DatabaseContainer;
import at.tuwien.repositories.MetadataRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
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