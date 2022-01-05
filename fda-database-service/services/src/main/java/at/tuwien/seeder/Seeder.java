package at.tuwien.seeder;

import at.tuwien.exception.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface Seeder {

    void seed() throws ImageNotSupportedException, AmqpException, ContainerNotFoundException, IOException, ContainerConnectionException;

}
