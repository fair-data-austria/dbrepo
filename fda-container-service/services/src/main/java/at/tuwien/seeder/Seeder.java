package at.tuwien.seeder;

import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import org.springframework.stereotype.Component;

@Component
public interface Seeder {

    void seed() throws DockerClientException, ImageNotFoundException;

}
