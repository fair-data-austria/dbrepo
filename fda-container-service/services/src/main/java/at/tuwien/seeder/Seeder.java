package at.tuwien.seeder;

import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public interface Seeder {

    @PostConstruct
    void seed() throws DockerClientException, ImageNotFoundException;

}
