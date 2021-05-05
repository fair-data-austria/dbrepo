package at.tuwien;

import at.tuwien.entities.container.image.ContainerImage;
import org.springframework.test.context.TestPropertySource;


@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseIntegrationTest {

    public final String DATABASE_1 = "postgres";

    public final ContainerImage image = null;

}
