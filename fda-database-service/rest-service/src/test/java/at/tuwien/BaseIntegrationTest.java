package at.tuwien;

import org.springframework.test.context.TestPropertySource;


@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseIntegrationTest {

    public final String DATABASE_1 = "postgres";

    public final ContainerImage image = null;

}
